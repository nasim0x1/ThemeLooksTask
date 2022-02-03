package themelooks.test.task.data.model

data class ProductModel(
    var id: String? = null,
    val title: String? = "",
    val description: String? = "",
    val variants: List<ProductVariant>? = null,
    val images: List<ImageModel>? = null,
    )
