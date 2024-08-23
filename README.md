# Robolectric

This repository contains the source of the documentation that lives at [robolectric.org](https://robolectric.org).

## Contributing

Make sure that you are on the `master` branch, and that it is up to date before making any changes. This is the default branch, so Git should put you there automatically.

Before submitting a Pull Request, run the documentation locally to check that the content and layout are correct. The documentation is built using [MkDocs](https://www.mkdocs.org/).

To do so, make sure that you have [Python 3+ installed](https://www.python.org/downloads/), and then install the required dependencies by running:

```bash
pip install -r requirements.txt
```

Then you can execute the following command to access the documentation locally at http://127.0.0.1:8000/:

```bash
mkdocs serve --open
```

Once your Pull Request is merged, the documentation will be automatically built and deployed by GitHub Actions.

## Javadocs

When a new version of Robolectric is released, the [`docs/javadoc`](docs/javadoc) directory needs to be updated. This can be achieved either automatically, or manually.

### Automatic publication

The simplest way to publish the javadoc for a specific version is to [create an issue](https://github.com/robolectric/robolectric.github.io/issues/new) whose title is `Publish javadoc for <version>`, where `<version>` is the version you want to deploy (for example `4.12`).

This will trigger the [`publish-javadoc.yml` workflow](.github/workflows/publish-javadoc.yml) to build and publish the corresponding javadoc.

> [!TIP]
> If you use the [`gh`](https://cli.github.com/) command line tool, you can use the following command:
> 
> `gh issue create --title "Publish javadoc for <version>" --body ""`

### Manual publication

To manually publish the javadoc, you can follow the guide in [Robolectric Wiki's release part](https://github.com/robolectric/robolectric/wiki/Performing-a-Release#update-docs).

## Deploy process

When a new PR is merged, GitHub Actions will build and push site code
to `gh-pages` branch. The repository has configured to deploy the site
with `gh-phages` branch, and then GitHub Pages will deploy the site
public automatically.
