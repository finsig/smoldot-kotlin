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
package io.finsig.smoldotkotlin

/**
 *  A class representing a Polkadot-based blockchain network.
 *
 *  @property specification the Chain Specification JSON Object for this chain.
 */
class Chain(val specification: ChainSpecification) {
    internal var id: ChainId? = null
}

/**
 * The name of the chain as defined in the specification.
 */
fun Chain.name(): String {
    return specification.getString("name")
}