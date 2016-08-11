#!/bin/bash
# git://10.0.2.2/aviato-profile

git daemon --export-all \
           --verbose \
           --reuseaddr \
           --base-path=$HOME/git \
           ~/git/aviato-profile
