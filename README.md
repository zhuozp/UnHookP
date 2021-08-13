# UnHookP
一种更简单，更简洁的资源热更新，支持单个activity下是否支持热更新，支持文本、颜色、背景色、图片热更新

#### 使用

gradle设置：

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
dependencies {
	        implementation 'com.github.zhuozp:UnHookP:v1.0.0'
	}
```

代码：

在合适的地方初始化：

如在Application：
```
HashMap<String, String> initRes = new HashMap<>();
initRes.put("btn_test2", "我的原始文本是--->这是初始文本2");
initRes.put("btn_test3", "我的原始文本是--->这是初始文本3");
HotResManager.getInstance().initRes(initRes);
```

在Activity中使用核心功能：

```
 @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * init ProxyResource when activity create
         * */
        proxyResource = new ProxyResource(getAssets(), getResources().getDisplayMetrics(), getResources().getConfiguration(), getResources());
        proxyResource.setUnHookPTextStrategy(new IUnHookPTextStrategy() {
            @Override
            public boolean adaptText() {
                return true;
            }
        });
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        DecorateFactory decorateFactory = new DecorateFactory(this, proxyResource);
        LayoutInflaterCompat.setFactory2(layoutInflater, decorateFactory);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotResManager.getInstance().update("btn_test1", "点击之后热更新的文本");
                button.setText(R.string.btn_test1);
            }
        });
    }

    private ProxyResource proxyResource;

    @Override
    public Resources getResources() {
        return proxyResource != null ? proxyResource : super.getResources();
    }
```

#### dex相关

生成dex文件类似如下：
~/Library/Android/sdk/build-tools/28.0.3/dx --dex --output hook.dex reflection/build/intermediates/intermediate-jars/debug/classes.jar
读取dex文件内容, 拷贝asset数据到本地存储的时候可以直接读取，dex文件为US_ASCII码格式
```
byte[] result = copyAssetFile(context, "hook.dex", code.getAbsolutePath());
DexFile dexFile = new DexFile(code);
dexFile = new DexFile(code);
String encodeDex = Base64.encodeToString(result, Base64.NO_WRAP);

```


#### 给个star赞吧
