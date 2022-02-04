## Implementing a product adding and showing system
---
### Description
---
#### This is a test project to implement few e-commerce app functionality
- Add product with unlimited variants(size,color,price)
- Show products list to user
- Show individual product details by clicking product


#### App activity's
- Home/Main activity
- Add product activity
- Product list activity
- Product description activity

### Api Endpoint : https://themelookstask-default-rtdb.firebaseio.com/products.json
###  [**Download APK**](apk/app-debug.apk?raw=true)

#### Package structure
```
.
└── java
    ├── data
    │   ├── model
    │   │   ├── ImageModel.kt               - Image model
    │   │   ├── ProductModel.kt             - Product model with 
    │   │   └── ProductVariant.kt           - Product variant model
    │   └── repository                      
    │       ├── AddProductRepository.kt     - Upload and save info to DB
    │       ├── SingleProductRepository.kt  - Fetch single product details from DB
    │       └── ViewProductRepository.kt    - Fetch All product list from DB
    ├── ui
    │   ├── adapter
    │   │   ├── productDes                  - All adapter used in description activity
    │   │   │   ├── ColorAdapter.kt         - Individual product color 
    │   │   │   ├── SizeAdapter.kt          - Individual product size
    │   │   │   └── SmallImageAdapter.kt    - Individual product images
    │   │   ├── ProductListAdapter.kt       - All product
    │   │   └── SelectedImgAdapter.kt       - Selected image for upload
    │   ├── view
    │   │   ├── admin               
    │   │   │   └── AdminHome.kt            - To add product 
    │   │   ├── MainActivity.kt             - To navigate user or admin activity
    │   │   └── user
    │   │       ├── ProductDetails.kt       - Individual product details activity
    │   │       └── UserHome.kt             - List of available in BD
    │   └── viewmodel
    │       ├── AddProductViewModel.kt      - For AdminHome activity
    │       ├── ProductDetailsViewModel.kt  - For ProductDetails activity
    │       └── UserHomeViewModel.kt        - For UserHome activity
    └── util                                - Helper package
        ├── Constants.kt                    - For all constants variable and functions
        ├── interfaces                      
        │   ├── ColorInterface.kt           - For ColorAdapter to handle color clicked
        │   ├── ImageInterface.kt           - For SmallImageAdapter to handle image clicked
        │   └── SizeInterface.kt            - For SizeAdapter to handle color clicked
        └── Response.kt                     - To handling DB operation response
```

### Used technology and dependency
 ---

- Database      - Firebase realtime database
- Storage       - Firebase cloud storage      
- Language      - Kotlin                   
- Architecture  - MVVM      

Dependency
-  firebase-storage     - To get storage access.
-  firebase-database    - To get database access.
-  viewmodel            - It will help to store and manage UI-related data in a lifecycle conscious way.
-  livedata             - To maintain the data state with regards to the component's lifecycle.
-  viewBinding          - For easily interacting with views.
-  glide                - For load image from urls.
-  shimmer              - For adding a shimmer effect to the views.
-  swiperefreshlayout   - It's layout. It detects the vertical swipe and perform defined actions


