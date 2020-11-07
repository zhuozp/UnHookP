# UnHookP
一种更简单，更简洁的资源热更新，支持单个activity下是否支持热更新，支持文本、颜色、背景色、图片热更新

一、文本如何设置
一般的，文本设置有以下形式：

xml设置：android:text="@string/xxx"

代码设置：textview.setText(context.getString(R.string.xxx))  或 textview.setText(context.getResource.getString(R.string.xxx)) 或textview.setText(R.string.xxx)

而要做到动态资源更新，也既是若能找到非内置到APP的相同资源名，应该保证这两种设置都能生效。

二、代码设置如何生效
无论是textview.setText(context.getString(R.string.xxx))  或 textview.setText(context.getResource.getString(R.string.xxx)) 或textview.setText(R.string.xxx)的哪种方式，

最终都经过Resource的getText方法获得得到，那么只要能把Resource代理成定义的Resource，并重写getText方法即可。

那么Resource如何替换成自定义的Resource？

这里就涉及Context，意为意境、上下文，简单来说就是起动串联的作用。Context好比一个人的思想，有了它，一个人才能知道通过什么途径获取相关的需求。

App应用中，就是通过Context获得相关资源实例Resource，然后根据资源实例获取自己的内容展示所示。

Context的继承关系如下所示，ContextImpl类似修饰类，ContextImpl中完成对Resource实例的获取，主要通过AsssetManager.getResource获取得到。

而Activity继承自Context，因此，我们在应用中调用getResource方法实际是从Activity的getResource方法开始，通过重写Activity的getResource方法，即可完成Resource的代理。

