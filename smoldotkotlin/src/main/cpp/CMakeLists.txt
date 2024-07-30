
#
# Copyright (C) The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cmake_minimum_required(VERSION 3.22.1)

project("smoldot-kotlin")

# configure import libs
set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../distribution)

add_library(lib_smoldot STATIC IMPORTED)
set_target_properties(lib_smoldot PROPERTIES IMPORTED_LOCATION
    ${distribution_DIR}/smoldot/lib/${ANDROID_ABI}/libsmoldot_c_ffi.a)

# build application's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")

add_library(smoldot-kotlin SHARED
        smoldot-kotlin.cpp)

target_include_directories(smoldot-kotlin PRIVATE
                           ${distribution_DIR}/smoldot/include)

target_link_libraries(smoldot-kotlin android lib_smoldot log)

