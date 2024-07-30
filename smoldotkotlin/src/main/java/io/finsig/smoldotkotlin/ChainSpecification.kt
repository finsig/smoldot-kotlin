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

import android.content.Context
import org.json.JSONObject

/**
 * Chain Specification JSON Object
 *
 * A Chain Specification is the collection of information that describes a Polkadot-based blockchain
 * network. For example, the chain specification identifies the network that a blockchain node
 * connects to, the other nodes that it initially communicates with, and the initial state that nodes
 *  must agree on to produce blocks.
 */
typealias ChainSpecification = JSONObject

/**
 * Read a Chain Specification from a Chain Specification Asset JSON file.
 */
fun ChainSpecification.readFromAsset(fileName: String, context: Context): JSONObject {
    val string = context.assets.open(fileName).bufferedReader().use{
        it.readText()
    }
    return JSONObject(string)
}

/**
 * The name of the chain name as defined in the specification file.
 */
fun ChainSpecification.name(): String {
    return getString("name")
}