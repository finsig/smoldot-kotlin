#! /bin/zsh -e

source ".scripts/functions.sh"

set -e

PACKAGE_VERSION=0.1.1

env::setup
env::build_configuration $1

rust::setup

pre_build::setup_smoldot_c_ffi

rust::build

post_build::create_distribution_directory
post_build::copy_abi_libraries_to_project

post_build::success
