#! /bin/zsh -e

source ".scripts/functions.sh"

DEFAULT_VERSION='0.1.1'

log::message "Building version $DEFAULT_VERSION"
/bin/zsh ".scripts/$DEFAULT_VERSION.sh" $1