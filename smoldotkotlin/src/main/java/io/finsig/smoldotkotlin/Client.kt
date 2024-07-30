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

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Exception

/**
 * Client that is used to connect to Polkadot-based blockchain networks.
 *
 * A shared singleton.
 */
class Client private constructor() {

    /**
     * Add a Chain to the Client.
     */
    fun add(chain: Chain): Chain {
        chain.id?.let {
            throw Exception("Chain has already been added.")
        }
        chain.id = smoldotAddWithSpecification(chain.specification.toString())
        return chain
    }

    /**
     * Remove a Chain from the Client.
     */
    fun remove(chain: Chain) {
        val id = chain.id ?: throw Exception("Chain not found in client.")
        smoldotRemoveWithId(id)
        chain.id = null
    }

    /**
     * Send a JSON-RPC2 request to a Chain.
     */
    fun send(jsonRPC2Request: JSONRPC2Request, chain: Chain) {
        val id = chain.id ?: throw Exception("Chain not found in client.")
        smoldotJSONRPC2Request(id, jsonRPC2Request.toJSONString())
    }

    /**
     * Asynchronous flow of response string values from the Client.
     */
    fun responses(chain: Chain) : Flow<String> = flow {
        while (isValid(chain)) {
            response(chain)?.let {
                emit(it)
            }
        }
    }

    /**
     * Suspend function to wait for response string from the Client.
     */
    suspend fun response(chain: Chain): String? {
        val id = chain.id ?: throw Exception("Chain not found in client.")
        val response = smoldotAwaitNextJSONRPC2Response(id)
        return if (response == null) { // critical - JNI response can return null
            return null
        } else {
            response
        }
    }

    /**
     * Check the status of the chain identifier in the Client.
     */
    fun isValid(chain: Chain): Boolean {
        chain.id?.let { id ->
            return smoldotIsValid(id)
        }
        return false
    }

    companion object {
        @Volatile
        private var _instance: Client? = null
        /**
         * Client singleton.
         */
        fun instance(): Client {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = Client()
                    }
                }
            }
            return _instance!!
        }
        init {
            System.loadLibrary("smoldot-kotlin")

            if (BuildConfig.DEBUG) {
                smoldotEnvironmentLogger(BuildConfig.RUST_LOG)
            }
        }
        private external fun smoldotAddWithSpecification(s: String): Long
        private external fun smoldotJSONRPC2Request(l: Long, s: String)
        private external suspend fun smoldotAwaitNextJSONRPC2Response(l: Long): String
        private external fun smoldotRemoveWithId(l: Long)
        private external fun smoldotIsValid(l: Long): Boolean
        private external fun smoldotEnvironmentLogger(s: String)
    }

}
