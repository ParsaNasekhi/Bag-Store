package com.parsanasekhi.store.model.repository.comment

import com.google.gson.JsonObject
import com.parsanasekhi.store.model.data.Comment
import com.parsanasekhi.store.model.net.ApiService

class CommentRepositoryImpl(
    private val apiService: ApiService
) : CommentRepository {

    override suspend fun getAllComments(productId: String): List<Comment> {
        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
        }
        val data = apiService.getAllComments(jsonObject)
        if (data.success) {
            return apiService.getAllComments(jsonObject).comments
        }
        return listOf()
    }

    override suspend fun addNewComment(
        productId: String,
        text: String,
        showMessage: (String) -> Unit
    ) {
        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
            addProperty("text", text)
        }
        val result = apiService.addNewComment(jsonObject)
        if (result.success) {
            showMessage(result.message)
        } else showMessage("Your comment didn't add!")
    }

}