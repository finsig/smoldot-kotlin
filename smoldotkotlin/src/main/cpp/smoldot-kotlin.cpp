// Smoldot Kotlin
// Copyright 2024 Finsig LLC
// SPDX-License-Identifier: Apache-2.0

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include <android/log.h>
#include "smoldot.h"
#include <jni.h>

#include <cinttypes>
#include <cstring>
#include <string>
#include <cstdbool>

extern "C"
JNIEXPORT jlong JNICALL
Java_io_finsig_smoldotkotlin_Client_00024Companion_smoldotAddWithSpecification(JNIEnv *env, jobject obj, jstring s) {
    auto chainSpec = env->GetStringUTFChars(s, nullptr);
    auto chainId = smoldot_add_chain(chainSpec);
    env->ReleaseStringUTFChars(s, chainSpec);
    return (long)chainId;
}

extern "C"
JNIEXPORT void JNICALL
Java_io_finsig_smoldotkotlin_Client_00024Companion_smoldotJSONRPC2Request(JNIEnv *env, jobject obj, jlong l, jstring s) {
    auto request = env->GetStringUTFChars(s, nullptr);
    smoldot_json_rpc_request(l, request);
    env->ReleaseStringUTFChars(s, request);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_finsig_smoldotkotlin_Client_00024Companion_smoldotAwaitNextJSONRPC2Response(JNIEnv *env, jobject obj, jlong l) {
    auto json = smoldot_wait_next_json_rpc_response(l);
    if (json == nullptr) {
        return (*env).NewStringUTF(nullptr);
    }
    auto response = (*env).NewStringUTF(json);
    smoldot_next_json_rpc_response_free(json);
    return response;
}

extern "C"
JNIEXPORT void JNICALL
Java_io_finsig_smoldotkotlin_Client_00024Companion_smoldotRemoveWithId(JNIEnv *env, jobject obj, jlong l) {
    smoldot_remove_chain(l);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_finsig_smoldotkotlin_Client_00024Companion_smoldotIsValid(JNIEnv *env, jobject obj, jlong l) {
    return smoldot_is_valid_chain_id(l);
}

extern "C"
JNIEXPORT void JNICALL
Java_io_finsig_smoldotkotlin_Client_00024Companion_smoldotEnvironmentLogger(JNIEnv *env, jobject obj, jstring s) {
    auto level = env->GetStringUTFChars(s, nullptr);
    smoldot_env_logger(level);
    env->ReleaseStringUTFChars(s, level);
}