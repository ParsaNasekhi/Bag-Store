package com.parsanasekhi.store.model.repository.comment

import com.parsanasekhi.store.model.data.Comment
import com.parsanasekhi.store.model.data.Product

interface CommentRepository {

    suspend fun getAllComments(productId: String): List<Comment>

    suspend fun addNewComment(productId: String, text: String, showMessage: (String) -> Unit)

}