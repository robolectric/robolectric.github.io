# Robolectric

[![Build Status](https://secure.travis-ci.org/robolectric/robolectric.github.io.png?branch=source)](http://travis-ci.org/robolectric/robolectric.github.io)

This repository is the source for the docs that live at [robolectric.org](http://robolectric.org).

## Contributing

Make sure you are in the `source` branch before making changes. This is the default branch so git should put you there automatically.

Before submitting a pull request, view the docs locally to check that the content and layout are correct. The docs are built using [middleman](https://github.com/middleman/middleman).

Assuming you don't already have [Bundler](http://bundler.io/) installed, you will first need to do:

    gem install bundler
    
The above command may require `sudo`.

Then run:

    bundle install
    bundle exec middleman server
  
This will build the docs and make them available at [localhost:4567](http://localhost:4567). Once
your pull request is accepted, the docs will be automatically built and pushed to the web by
Travis CI.

## Javadocs

When Robolectric is released, the `source/javadocs` directory needs to be updated. This can be done by grabbing the javadocs that were pushed to [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.robolectric%22%20AND%20a%3A%22robolectric%22) and unjarring them into that directory.

## Gory Details: How These Docs Get Published

This repository has 2 branches: `source` and `master`. The former is a middleman project that can generate the latter. With every commit that is made to the `source` branch, Travis CI runs middleman and deploys the results back to the `master` branch. It does this by using an OAUTH token belonging to the robolectric-travis-bot user, who only has permission to commit and push to this repository.




