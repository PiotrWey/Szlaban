> [Go back](README.md)

# Contribution Guidelines

Thanks for your interest in contributing to _Szlaban_! Before you get started, make sure you've read the
[Developing](README.md#developing) section of the README &ndash; this document only covers the guidelines your
contribution should follow, and won't explain much else.

## Contents

- [Commit Etiquette](#1-commit-etiquette)
- [Code Style](#2-code-style)
- [Licence Headers](#3-licence-headers)
- [Project Structure](#4-project-structure)
- [Generative AI](#5-generative-ai)
- [Testing](#6-testing)
- [Pull Requests & New Features](#7-pull-requests--new-features)

## 1. Commit Etiquette

Your commits should be _meaningful_, and represent _concrete_ changes. Before you commit, make sure it:

1. Actually contributes something. Whitespace-only changes, line ending conversions, and similar are not acceptable.
2. Touches the least amount of existing code it needs to function. You may edit existing files to integrate your
   changes, but you must not reformat files wholesale just because you added a few lines to them.
3. Has a clear, descriptive commit message that succinctly summarises what changed. If you need more space, use the
   extended description &ndash; just make sure the summary line is still meaningful on its own.
4. Is internally coherent. If you created a method and then rewrote it several times to fix bugs, that should
   probably be a single commit rather than several.
5. Has been at least minimally tested. You are not required to write unit tests for every change, but your commit
   should not introduce obvious bugs or regressions.
6. Doesn't change unrelated things. Optimisations and refactors should be wholly separate from feature work.
7. Doesn't alter project metadata, dependencies, or dependency versions without a clear justification.

## 2. Code Style

Follow the existing code style, formatting, and conventions throughout. At the end of the day, this is my project,
and it should feel like it is.

1. Indent with spaces, 2 per level, with a continuation indent of 4. This should be apparent from the existing code,
   but if not, adjust your IDE settings accordingly.
2. Use **British English** throughout &ndash; this includes comments, method names, variable names, and default
   values. Where external API fields use other spellings (e.g. `color`, `initialize`), use them as-is, but any
   code that is part of this plugin must use British spellings consistently.
3. Follow the singular/plural naming convention. A class that represents a single concrete item should be named in the
   singular (e.g. `Command`). Classes that act as factories, managers, or registries for multiple items should be
   plural or suffixed accordingly (e.g. `CommandManager`).
4. If you notice a pattern in the codebase that isn't documented here, follow it anyway &ndash; and feel free to
   raise an issue if you think it should be.

## 3. Licence

> [!IMPORTANT]
> By submitting a contribution, you agree that your code will be licensed under the same terms as the rest of the
> project.

All source files under `src/main/java/` must include the following licence header at the top of the file, before the
`package` declaration:

```java
/*
 * Copyright (c) <YEAR> Piotr Weychan [and contributors]
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */
```

Replace `<YEAR>` with the year the file was created. IntelliJ IDEA should handle this automatically if configured
correctly. The `[and contributors]` suffix should be included if the file has received contributions from people
other than the original author.

## 4. Project Structure

Adhere to the existing project structure.

1. All code must live in the `dev.piotr_weychan.szlaban` package or an appropriate subpackage thereof.
2. Subpackage names should be singular (e.g. `module`, `command`, not `modules`, `commands`).
3. Use the existing extension points. If there is an established way to add functionality &ndash; such as the module
   or behaviour systems &ndash; use it rather than working around it. See [Extending](docs/extending.md) for details.
4. Avoid code duplication. Commonly used logic should be extracted into `util`, and inheritance should be preferred
   over copying and modifying existing classes.

## 5. Generative AI

Generative AI tools are permitted in a supporting capacity &ndash; for instance, spotting bugs, suggesting
improvements, or helping with boilerplate. However, you must not submit contributions where significant portions of
the code are AI-generated. AI-generated code tends to be inconsistent in style, difficult to read, and often
introduces subtle bugs that are hard to spot &ndash; all of which make it harder to maintain and review.

If you do make use of AI-generated code, you must be able to fully explain what it does and why it is correct. Code
that is clearly AI-generated in structure or style, or that contains AI-typical bugs, is likely to be rejected or
returned for revision.

For documentation, the bar is lower &ndash; AI assistance is fine as long as the result is accurate, coherent, and
consistent with the existing writing style.

## 6. Testing

### Unit Tests

Where your contribution contains logic that is complex enough to warrant unit tests, it should be designed to be
loosely coupled from the module system so that it can be tested independently. The base classes for `Module`s and
`Behaviour`s already have unit tests written to cover their behaviour, so you don't need to re-test anything that is
already covered by them.

### Integration Testing

_Szlaban_ is a Minecraft plugin, and the best way to verify that your contribution works correctly is by testing it
on a real server. Before submitting a pull request, you should have a clear idea of what your changes do, what
correct behaviour looks like, and what the potential points of failure are. To help you come up with a testing
strategy, try to answer the following questions:

1. What does your change do?
2. What does correct behaviour look like?
3. What does incorrect behaviour look like?
4. What specific situations does your change apply in?

Once you have answers, aim to recreate those situations on a test server and verify that everything behaves as
expected.

> [!TIP]
> Some changes may require multiple players to test properly. In those cases, it's worth recruiting a friend or two
> to help you out.

## 7. Pull Requests & New Features

You don't need prior approval to work on a new feature, but it's always a good idea to open an issue or get in touch
early &ndash; it helps avoid duplicated effort, and means you can get feedback before you've already written
everything.

If you begin work on a new feature, consider opening a _Draft Pull Request_ so that progress is visible and early
feedback can be given. Once your contribution is ready, promote it to a normal pull request and make sure it:

1. Adheres to all the guidelines above.
2. Is feature-complete, or the scope is clearly defined and agreed upon.
3. Has been tested on a real server.
4. Has a clear description of what it does and why.

Pull requests that don't meet these criteria may be delayed or rejected. If they're all ticked off &ndash;
congratulations, there's a good chance it'll make it into the next release!