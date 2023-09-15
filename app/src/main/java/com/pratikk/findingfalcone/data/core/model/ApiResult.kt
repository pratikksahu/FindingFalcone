package com.pratikk.findingfalcone.data.core.model

sealed interface ApiResult<T:Any>{
    val isApiError
        get() = this is ApiError
}

class ApiSuccess<T:Any>(val data:T):ApiResult<T>
class ApiError<T:Any>(val error:String? = null):ApiResult<T>

suspend fun <T:Any>ApiResult<T>.onSuccess(
    executable:suspend (data:T) -> Unit
):ApiResult<T> = apply{
    if(this is ApiSuccess)
        executable(data)
}
suspend fun <T:Any>ApiResult<T>.onError(
    executable:suspend (String?) -> Unit
):ApiResult<T> = apply{
    if(this is ApiError)
        executable(error)
}