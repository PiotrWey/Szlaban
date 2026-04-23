/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall.filter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class LookupRuleEvaluator implements RuleEvaluator {
  private final Map<String, Set<String>> rules;
  private final String apiEndpoint;

  public LookupRuleEvaluator(Map<String, Set<String>> rules, String apiEndpoint) {
    this.rules = rules;

    if (!apiEndpoint.contains("%s"))
      throw new IllegalArgumentException("API endpoint must be a valid Java format-string");
    else if (!apiEndpoint.matches("^https?://.*"))
      throw new IllegalArgumentException("API endpoint must be a valid HTTP/S URL");

    this.apiEndpoint = apiEndpoint;

  }

  /**
   * Gets the JSON entry at the dot-separated path
   * @param obj the JSON object to search
   * @param path the path to find
   * @return the found object
   */
  @Nullable
  private static JsonElement getJsonElement(JsonObject obj, String path) {
    String[] parts = path.split("\\.");

    JsonElement current = obj;

    for  (String part : parts) {
      if (current == null || !current.isJsonObject()) return null;

      current =  current.getAsJsonObject().get(part);
    }

    return current;
  }

  @Override
  public RuleType evaluate(InetAddress address) throws RuleEvaluationException {
    // Build the URL to look up the ip
    String encodedAddress = URLEncoder.encode(address.getHostAddress(), StandardCharsets.UTF_8);
    URI lookupApi = URI.create(apiEndpoint.formatted(encodedAddress));

    // Make the web request with HttpClient

    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(lookupApi)
          .GET()
          .build();

      HttpResponse<String> response = client.send(
          request,
          HttpResponse.BodyHandlers.ofString()
      );

      // Convert to JSON
      JsonObject json = JsonParser
          .parseString(response.body())
          .getAsJsonObject();

      // Evaluate rules on the JSON
      for (Map.Entry<String, Set<String>> entry : rules.entrySet()) {
        String path = entry.getKey();
        Set<String> values = entry.getValue();

        // Find the element at path
        JsonElement jsonElement = getJsonElement(json, path);

        System.out.println(path + " " + values.toString() + " " + jsonElement);

        // Skip if key not found
        if (jsonElement == null || !jsonElement.isJsonPrimitive()) continue;

        if (values.contains(jsonElement.getAsJsonPrimitive().getAsString())) return RuleType.BLOCK;
      }

    } catch (IOException | InterruptedException e) {
      throw new RuleEvaluationException("Error while reading URL", e);
    }

    return RuleType.CONTINUE;
  }
}
