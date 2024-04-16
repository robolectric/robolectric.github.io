# Robolectric

This repository contains the source of the documentation that lives at [robolectric.org](http://robolectric.org).

## Contributing

Make sure that you are on the `master` branch, and that it is up to date before making any changes. This is the default branch, so Git should put you there automatically.

Before submitting a Pull Request, run the documentation locally to check that the content and layout are correct. The documentation is built using [MkDocs](https://www.mkdocs.org/).

To do so, make sure that you have [Python 3+ installed](https://www.python.org/downloads/), and then install the required dependencies by running:

```bash
pip install -r requirements.txt
```

Then you can execute the following command to access the documentation locally at http://127.0.0.1:8000/:

```bash
mkdocs serve
```

Once your Pull Request is merged, the documentation will be automatically built and deployed by GitHub Actions.

## Javadocs

When a new version of Robolectric is released, the `javadoc` directory needs to be updated. We can get the last steps to generate the Javadocs in [Robolectric Wiki's release part](https://github.com/robolectric/robolectric/wiki/Performing-a-Release#release).
