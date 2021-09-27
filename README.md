# Robolectric

[![Build Status](https://secure.travis-ci.org/robolectric/robolectric.github.io.png?branch=master)](http://travis-ci.org/robolectric/robolectric.github.io)

This repository is the source for the docs that live at [robolectric.org](http://robolectric.org).

## Contributing

Make sure you are in the `master` branch before making changes. This is the default branch so git should put you there automatically.

Before submitting a pull request, view the docs locally to check that the content and layout are correct. The docs are built using [jekyll](https://jekyllrb.com/).

Assuming you don't already have [Bundler](http://bundler.io/) installed, you will first need to do:

    gem install bundler
    
The above command may require `sudo`.

Then run:

    bundle install
    bundle exec jekyll server --incremental
  
This will build the docs and make them available at [localhost:4000](http://localhost:4000). Once
your pull request is accepted, the docs will be automatically built and pushed to the web by GitHub.

## Javadocs

When Robolectric is released, the `source/javadocs` directory needs to be updated. We can get latest steps to generate javadocs at [Robolectric Wiki's release part](https://github.com/robolectric/robolectric/wiki/Performing-a-Release#release).
