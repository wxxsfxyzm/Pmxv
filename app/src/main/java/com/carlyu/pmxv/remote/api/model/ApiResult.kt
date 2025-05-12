package com.carlyu.pmxv.remote.api.model

import com.carlyu.pmxv.remote.exception.ProxmoxClientException

/**
 * A generic sealed class to wrap API call results.
 * Represents either a successful outcome with data or a failure with a specific exception.
 */
sealed class ApiResult<out T> {

    /**
     * Represents a successful API call result with the retrieved data.
     * @param data The successful response data (business model, not DTO).
     */
    data class Success<out T>(val data: T) : ApiResult<T>()

    /**
     * Represents a failed API call result with details about the error.
     * @param exception The type of exception that occurred during the operation.
     */
    data class Failure(val exception: ProxmoxClientException) : ApiResult<Nothing>()
}