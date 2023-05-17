# PagingAdapter
  Paging3 RecyclerView Adapter

## 超简单易用的使用Paging3封装的RecyclerView的列表适配器

### 本库的优势：

- 使用paging3封装，具有paging3完全优势，快速实现列表分页，无缝加载；
- 可以对条目实现增删改，弥补paging无法修改数据问题；
- 不需要实现paging的数据库缓存，直接网络请求即可；
- 多类型条目解耦，新增新的条目类型完全不需要修改原有代码，直接新建holder即可；
- 完全不需要自己实现adapter了，每种条目写一个简单的holder即可实现列表；
- 支持DataBinding和布局id设置数据；



## 使用步骤：


### 依赖方式：
1. 项目的根build.gradle里添加jitpack仓库
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. 依赖：在模块build.gradle添加
```
dependencies {
	    implementation 'com.github.jarryleo:PagingAdapter:3.1.5'
}
```

### 代码使用：

#### 1. 数据模型bean类实现接口 DifferData
```
//用作数据比较，执行RecyclerView条目动画,可以不实现方法,但是不建议这样做,性能差距就在这里体现
interface DifferData {
    fun areItemsTheSame(d: DifferData): Boolean
    fun areContentsTheSame(d: DifferData): Boolean
    fun getChangePayload(d: DifferData): Any?
}
```

#### 2. 实现条目holder：继承SimpleHolder,用DataBinding或者也可直接使用布局中的id对view执行操作，放心使用，view可以复用

```
class NewsHolder : SimpleHolder<NewsBean.StoriesBean>(R.layout.item_news) {
    override fun bindItem(
        item: ItemHelper,
        data: NewsBean.StoriesBean,
        payloads: MutableList<Any>?
    ) {
        item.binding<ItemNewsBinding>()?.data = data
    }
}
```

#### 3. 设置Adapter

```
 recyclerView.adapter = SimplePagingAdapter(NewsHolder(), TitleHolder())

```
##### dsl 用法：
```
binding.adapter = binding.rvNews.buildAdapter {
            addHolder(NewsHolder()) {
                onItemClick {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.data.url)))
                }
                onItemChildClick(R.id.iv_cover) {
                    toast(it.data.title)
                }
            }
            addHolder(TitleHolder(), true) {
                onItemClick {
                    toast(it.data.title)
                }
            }
            addHolder(PlaceHolder())
            setPager(model.pager)
        }
```

#### 4. 给adapter设置数据

```
//在ViewModel类里面实现
val pager = SimplePager<Long, DifferData>(viewModelScope) {
        val date = it.key ?: initialKey
        try {
            //从网络获取数据
            val data = api.getNews(date).await()
            //返回数据
            PagingSource.LoadResult.Page(data.stories, null, data.date?.toLongOrNull())
        } catch (e: Exception) {
            //请求失败
            PagingSource.LoadResult.Error(e)
        }
    }


//绑定数据源
adapter.setPager(model.pager)
```

`注意：不要使用adapter默认的 submitData,否则无法修改数据`


**使用就是这么方便，其中一些定义需要对paging3有一定了解，更多功能请看范例**

[Paging3用法详解和踩坑经验](https://juejin.im/post/6885983953463803911)
