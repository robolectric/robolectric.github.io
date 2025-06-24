# Robolectric

This repository contains the source of the documentation that lives at
[robolectric.org](https://robolectric.org).

## Contributing

Make sure that you are on the `main` branch, and that it is up to date before making any changes.
This is the default branch, so Git should put you there automatically.

### Build the documentation locally

Before submitting a Pull Request, run the documentation locally to check that the content and layout
are correct. The documentation is built using [MkDocs](https://www.mkdocs.org/).

To do so, make sure that you have [Python 3+ installed](https://www.python.org/downloads/), and then
install the required dependencies by running:

```bash
pip install -r requirements.txt
```

Then you can execute the following command to access the documentation locally at
[http://127.0.0.1:8000/](http://127.0.0.1:8000/):

```bash
mkdocs serve --open
```

### Using code snippets

The code snippets displayed throughout the website are stored in the [`snippets`](snippets) Android
project.

To use a new code snippet, follow these steps:

1. Define your code snippet in the `snippets/java` module.
2. Surround it with

```java
// --8<-- [start:my_code_snippet_identifier]
my code snippet
// --8<-- [end:my_code_snippet_identifier]
```

<!-- markdownlint-disable-next-line MD029 -->
3. To use it in your Markdown file, use the following syntax:

````markdown
```java
--8<-- "snippets/java/path/to/my/snippet/MyCodeSnippet.java:my_code_snippet_identifier"
```
````

> [!NOTE]
>
> The migration of the code snippets to the `snippets` project is a work in progress.
> New code snippets **should** be added in the `snippets` project.

### Validate your Markdown files

If you modified any Markdown file, we recommend using
[`DavidAnson/markdownlint-cli2`](https://github.com/DavidAnson/markdownlint-cli2) to ensure that the
formatting rules are respected.

Once installed, you can run the command below to perform the check. Add the `--fix` option to fix
issues that can be addressed automatically. The non-resolved issues will be printed in the console.

```bash
markdownlint-cli2 "README.md" "docs/**/*.md" "#docs/javadoc/**/*.md" --config .markdownlint.jsonc
```

Once your Pull Request is merged, the documentation will be automatically built and deployed by
GitHub Actions.

## Javadocs

When a new version of Robolectric is released, the [`docs/javadoc`](docs/javadoc) directory needs to
be updated. This can be achieved either automatically, or manually.

### Automatic publication

The simplest way to publish the javadoc for a specific version is by running the
[`publish-javadoc.yml`](.github/workflows/publish-javadoc.yml) workflow from the repository's
[Actions](https://github.com/robolectric/robolectric.github.io/actions/workflows/publish-javadoc.yml)
tab.

### Manual publication

To manually publish the javadoc, you can follow the guide in
[Robolectric Wiki's release part](https://github.com/robolectric/robolectric/wiki/Performing-a-Release#update-docs).

## Deploy process

When a PR is merged, GitHub Actions will build and push the site code to the `gh-pages` branch. The
repository is configured to deploy the site with `gh-pages` branch, and then GitHub Pages will
deploy the site automatically.
