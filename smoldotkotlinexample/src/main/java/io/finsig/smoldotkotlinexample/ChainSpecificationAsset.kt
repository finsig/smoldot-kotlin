/**
 * SmoldotKotlin
 * Copyright 2024 Finsig LLC
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.finsig.smoldotkotlinexample

/**
 * A Chain Specification asset file.
 *
 * @property fileName the name of the asset file.
 */
enum class ChainSpecificationAsset(val fileName: String) {
    POLKADOT("polkadot.json"),
    KUSAMA("kusama.json"),
    ROCOCO("rococo.json"),
    WESTEND("westend.json")
}