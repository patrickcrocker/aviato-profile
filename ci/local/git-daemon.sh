#!/bin/bash

git daemon --export-all \
           --verbose \
           --reuseaddr \
           --base-path=$HOME/git \
           ~/git/aviato-profile
