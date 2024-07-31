#! /bin/zsh

log::error() {
    echo "\033[0;31m$@\033[0m" 1>&2
}

log::success() {
    echo "\033[0;32m$@\033[0m"
}

log::message() {
    echo "\033[0;36m▸ $@\033[0m"
}

log::info() {
    echo "\033[1;37m$@\033[0m"
}

env::setup() {
    log::message "Setting up env"
    if [ -z $PACKAGE_VERSION ]; then
        log::error 'Must specify $PACKAGE_VERSION'
        exit -1
    fi
    log::info "PACKAGE_VERSION=$PACKAGE_VERSION"
    export SMOLDOTKOTLIN_VERSION=${SMOLDOTKOTLIN_VERSION:-$PACKAGE_VERSION}
    log::info "SMOLDOTKOTLIN_VERSION=$SMOLDOTKOTLIN_VERSION"
    export ROOT_DIRECTORY=${ROOT_DIRECTORY:-`pwd`}
    log::info "ROOT_DIRECTORY=$ROOT_DIRECTORY"
    export DISTRIBUTION_DIRECTORY=${DISTRIBUTION_DIRECTORY:-"$ROOT_DIRECTORY/distribution/smoldot/lib"}
    log::info "DISTRIBUTION_DIRECTORY=$DISTRIBUTION_DIRECTORY"
    export RUST_TOOLCHAIN=${RUST_TOOLCHAIN:-'nightly-2024-06-30'}
    log::info "RUST_TOOLCHAIN=$RUST_TOOLCHAIN"
    
    export CHECKOUTS_DIRECTORY=${CHECKOUTS_DIRECTORY:-"$ROOT_DIRECTORY/.build/checkouts"}
    log::info "CHECKOUTS_DIRECTORY=$CHECKOUTS_DIRECTORY"
    export FFI_DIRECTORY=${FFI_DIRECTORY:-"$CHECKOUTS_DIRECTORY/smoldot-c-ffi"}
    log::info "FFI_DIRECTORY=$FFI_DIRECTORY"

}

env::build_configuration() {
    if [ -z $1 ]; then
        BUILD_CONFIG="release"
    elif [ $1 = "debug" ]; then
        BUILD_CONFIG="debug"
    else
        BUILD_CONFIG="release"
    fi

    log::message "Building for $BUILD_CONFIG"
    export BUILD_CONFIG
}



pre_build::setup_smoldot_c_ffi() {
    log::message "Checkout smoldot-c-ffi version $SMOLDOT_VERSION"
    if [ ! -d $FFI_DIRECTORY ]; then
        git clone https://github.com/finsig/smoldot-c-ffi $FFI_DIRECTORY
    fi
    
    cd $FFI_DIRECTORY
    git fetch --all
    #git checkout -f tags/$SMOLDOT_VERSION
}


rust::toolchain() {
    log::message "Install $RUST_TOOLCHAIN toolchain"
    rustup toolchain install $RUST_TOOLCHAIN
}

rust::targets() {
    log::message "Install targets"
    rustup target add --toolchain $RUST_TOOLCHAIN aarch64-linux-android
    rustup target add --toolchain $RUST_TOOLCHAIN armv7-linux-androideabi
    rustup target add --toolchain $RUST_TOOLCHAIN i686-linux-android
    rustup target add --toolchain $RUST_TOOLCHAIN x86_64-linux-android
}

rust::update() {
	rustup update
}

rust::setup() {
    rust::toolchain
    rust::targets
    rust::update
}

rust::build_target() {
    log::message "Build $1"
    if [ -z $1 ]; then
        log::error 'Must specify target as input to rust::build_target'
        exit -1
    fi
    cargo +$RUST_TOOLCHAIN build $([[ $1 = "aarch64-linux-android" ]] && [[ $RUST_TOOLCHAIN == "nightly"* ]] && echo "-Z build-std") --lib --package smoldot-c-ffi --target $1 $([[ $BUILD_CONFIG = "release" ]] && echo --release)
}

rust::build() {
	rust::build_target aarch64-linux-android
	rust::build_target armv7-linux-androideabi
	rust::build_target i686-linux-android
	rust::build_target x86_64-linux-android
}

post_build::create_distribution_directory() {

    if [ ! -d "$DISTRIBUTION_DIRECTORY/arm64-v8a/" ]; then
        log::message "Creating distribution directory"
        mkdir -p $DISTRIBUTION_DIRECTORY/arm64-v8a/
    fi
    if [ ! -d "$DISTRIBUTION_DIRECTORY/armeabi-v7a/" ]; then
        log::message "Creating distribution directory"
        mkdir -p $DISTRIBUTION_DIRECTORY/armeabi-v7a/
    fi
    if [ ! -d "$DISTRIBUTION_DIRECTORY/x86/" ]; then
        log::message "Creating distribution directory"
        mkdir -p $DISTRIBUTION_DIRECTORY/x86/
    fi
    if [ ! -d "$DISTRIBUTION_DIRECTORY/x86_64/" ]; then
        log::message "Creating distribution directory"
        mkdir -p $DISTRIBUTION_DIRECTORY/x86_64/
    fi
}

post_build::copy_abi_libraries_to_project() {
    log::message "Copy ABI static library files to project"
	log::info "arm64-v8a"
    cp -r $FFI_DIRECTORY/target/aarch64-linux-android/$BUILD_CONFIG/libsmoldot_c_ffi.a $DISTRIBUTION_DIRECTORY/arm64-v8a/
    log::info "armeabi-v7a"
    cp -r $FFI_DIRECTORY/target/armv7-linux-androideabi/$BUILD_CONFIG/libsmoldot_c_ffi.a $DISTRIBUTION_DIRECTORY/armeabi-v7a/
    log::info "x86"
    cp -r $FFI_DIRECTORY/target/i686-linux-android/$BUILD_CONFIG/libsmoldot_c_ffi.a $DISTRIBUTION_DIRECTORY/x86/
    log::info "x86_64"
    cp -r $FFI_DIRECTORY/target/x86_64-linux-android/$BUILD_CONFIG/libsmoldot_c_ffi.a $DISTRIBUTION_DIRECTORY/x86_64/
}

post_build::success() {
    log::success ''
    if [ $BUILD_CONFIG = "release" ]; then
        log::success "Built artifacts can be found at $DISTRIBUTION_DIRECTORY"
    fi
}

docker::build() {
	log::message "Build Docker container"
	cd $ROOT_DIRECTORY
	docker build -t "finsig:Dockerfile" .
}

docker::run() {
	log::message "Build smoldotkotlin library"
	docker run --rm -v `pwd`:/project finsig:Dockerfile bash -c 'cd /project; ./gradlew smoldotkotlin:bundleReleaseAar'
}

docker::success() {
	log::success "▸ BUILD SUCCESSFUL!"
	log::success ''
	log::success "AAR can be found at $ROOT_DIRECTORY/smoldotkotlin/build/outputs/aar"
}
