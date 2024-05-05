## Android 小项目 音乐播放器 MusicPlayer

### 一、题目

**音乐播放器：** 

<img src="https://gitee.com/RoseTame/pic/raw/master/blog/202405022325983.jpg" >

**主要要求：** 界面模仿上图 

◼ 音乐文件：放在项目 assets/music 目录下 

◼ 主窗口 Activity+播放列表 ListView+音乐进度 SeekBar+播放 ExoPlayer+后台 Service 

◼ 只需完成的功能：播放/暂停、上一首、下一首(自动下一首)、单曲循环、播放列表选择

**项目地址:**

https://github.com/RoseTame/MusicPlayer

**软件下载地址:**

 https://github.com/RoseTame/MusicPlayer/releases/tag/v1.0.0

### 二、 界面展示

<img src="https://gitee.com/RoseTame/pic/raw/master/blog/202405031555785.png" width="30%">

> 1. ActionBar
> 2. Imageview表示歌曲图片
> 3. 3个TextView表示歌名、歌手名和歌词，其中歌名会随着歌曲变化而变化
> 4. 6个ImageButton（不实现功能），5个ImageButton（实现功能）
> 5. Seekbar+TextView表示歌曲进度

### 三、Prepare

#### 1. 新建项目MusicPlayer

![image-20240505180800087](https://gitee.com/RoseTame/pic/raw/master/blog/202405051808136.png)

![image-20240505180855309](https://gitee.com/RoseTame/pic/raw/master/blog/202405051808353.png)

#### 2. 下载必要资源

##### 2.1 添加ExoPlayer依赖

**2.1.1. 打开Project Structure**

![image-20240505181911517](https://gitee.com/RoseTame/pic/raw/master/blog/202405051819544.png)

**2.1.2 选择依赖Dependencies -> app -> Library Dependency** 

![image-20240505182105422](https://gitee.com/RoseTame/pic/raw/master/blog/202405051821496.png)

**2.1.3.搜索com.google.android.exoplayer:exoplayer**

![image-20240505182323115](https://gitee.com/RoseTame/pic/raw/master/blog/202405051823164.png)

##### 2.2 下载界面所需图标

**下载地址：** https://github.com/RoseTame/MusicPlayer/tree/main/app/src/main/res/drawable

> 下载后将所有图片复制到drawable文件夹里

#### 3. 新建assets/music文件夹

##### 3.1 项目右键->new->Folder->Assets Folder

![image-20240505182554806](https://gitee.com/RoseTame/pic/raw/master/blog/202405051825856.png)

 ##### 3.2 assets右键->new->Directory

![image-20240505182632670](https://gitee.com/RoseTame/pic/raw/master/blog/202405051826730.png)

 ##### 3.3 命名为music

![image-20240505182700693](https://gitee.com/RoseTame/pic/raw/master/blog/202405051827719.png)

音乐文件下载：https://github.com/RoseTame/MusicPlayer/tree/main/app/src/main/assets/music

### 四、界面设计

#### 1. 添加颜色

向color.xml添加颜色值

```html
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
</resources>
```

#### 2. 添加主题

##### 2.1 向theme.xml中添加主题Theme.Player

```html
<style name="Theme.Player" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
    <!-- Primary brand color. -->
    <item name="colorPrimary">@android:color/holo_orange_dark</item>
    <item name="colorPrimaryVariant">@color/purple_700</item>
    <item name="colorOnPrimary">@color/white</item>
    <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
</style>

```

##### 2.2 修改Manifest改变主题

```css
android:theme="@style/Theme.Player"
```

#### 3. 设置全屏和ActionBar

##### 3.1 设置全屏显示

```java
// 设置全屏
getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
);
```

##### 3.2 设置ActionBar

```java
// 设置 ActionBar
actionBar = getSupportActionBar();
actionBar.setTitle("MusicPlayer");
actionBar.setDisplayHomeAsUpEnabled(true);
```

详细activity_main见代码部分

### 五、ListView

#### 1. 效果展示

![image-20240505183506819](https://gitee.com/RoseTame/pic/raw/master/blog/202405051835863.png)

**功能介绍：**

> 1. 点击歌单按钮，调出ListView界面
> 2. 点击歌曲，高亮显示，并对音乐进行播放(播放功能后续实现)
> 3. 点击返回或者ListView上方空白处，收起ListView界面

#### 2. API

| API         | Explanation                       |
| ----------- | --------------------------------- |
| getMusic()  | 返回音乐列表List<String>          |
| showList()  | 点击列表ImageButton，唤出ListView |
| playMusic() | 播放在ListView里选中的音乐        |

##### 2.1 getMusic()

​        遍历fNames数组，添加到mList当中，并添加一个返回键        

```java
List<String> getMusic() {
   List<String> mList = new ArrayList<>();
   try {
     String[] fNames = getAssets().list("music");
     for (String fn : fNames) {
       mList.add(fn);
     }
   } catch (IOException e) {
     throw new RuntimeException(e);
   }
   mList.add("返回");
   return mList;
 }
```

##### 2.2 showListView()

> 创建适配器adpter:
>
> 1. 上下文
> 2. 选择模式
> 3. List

```java
public void showListView() {
   music_list = getMusic(); // 获取音乐列表
   adpter = new ArrayAdapter<String>( // 创建适配器
      MainActivity.this,
       android.R.layout.simple_list_item_single_choice,
       music_list
   );
   listView.setAdapter(adpter); // 设置适配器
   listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 设置选择模式

  listView.setOnItemClickListener(listener2); // 设置监听器
  listView.setVisibility(View.VISIBLE); // 设置可见
}
```

##### 3.3 监听ListView

> 1. 当点击的是“返回”时，将ListView的可见性调成GONE,将其收起;
> 2. 当点击的是歌曲时，播放歌曲

```java
AdapterView.OnItemClickListener listener2 = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView lv = (ListView) parent;
        lv.setSelector(R.color.purple_200); //设置高亮背景色 purple_200颜色值见原来项目
        String musicName = parent.getItemAtPosition(position).toString(); //获得选中项图片名称，需要toString

        if (musicName.equals("返回")) {
            // 获取ListView的布局参数
            listView.setVisibility(View.GONE);
        } else {
            playMusic(musicName);
        }
    }
};
```
#####  3.4 playMusic

> 1. 开始计时（联合后续的seekBar）
> 2. 调用MusicService中的playMusic(String)播放音乐
> 3. 更换ImageButton图片

```java
public void playMusic(String musicName) {
    updateSongName(musicName);
    if (isServiceBound && musicService != null) {
        timer = new Timer();
        timer.schedule(new ProgressUpdate(), 0, 1000);
        musicService.playMusic(musicName);
        btn_play.setImageResource(R.drawable.pause);
    }
}
```

##### 3.5 监听View

> 当点击区域不在ListView内，将ListView的可见性设置为View.GONE，将其收起

```
layout.setOnTouchListener(new View.OnTouchListener() {
    public boolean onTouch(View v, MotionEvent event) {
        // 获取 ListView 的位置信息
        int[] listViewLocation = new int[2];
        listView.getLocationOnScreen(listViewLocation);

        // 判断触摸事件的坐标是否在 ListView 区域内
        if (event.getRawX() < listViewLocation[0] ||
                event.getRawX() > listViewLocation[0] + listView.getWidth() ||
                event.getRawY() < listViewLocation[1] ||
                event.getRawY() > listViewLocation[1] + listView.getHeight()) {
            // 如果触摸事件不在 ListView 区域内，则隐藏 ListView
            listView.setVisibility(View.GONE);
            return true; // 表示触摸事件已经被处理
        }
        return false; // 表示触摸事件未被处理
    }
});
```

##### 3.6 监听ImageButton

```java
View.OnClickListener listener1 = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        switch (v.getId()) {
            case R.id.btn_playList:
                showListView();
                break;
            case R.id.btn_play:
                playOrPauseMusic();
                break;
            case R.id.btn_pre:
                playPreviousTrack();
                break;
            case R.id.btn_next:
                playNextTrack();
                break;
            case R.id.btn_playWay:
                changePlayMode();
                break;
        }
    }
};
```

### 六、 MusicService

#### 1. 新建MusicService

##### 1.1 选中包->new->Service->Service
![image-20240505194049827](https://gitee.com/RoseTame/pic/raw/master/blog/202405051940888.png)

##### 1.2 命名为MusicService
![image-20240505194143776](https://gitee.com/RoseTame/pic/raw/master/blog/202405051941826.png)

#### 2. **绑定**Service

```java
// 绑定 MusicService
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isServiceBound = false;
        }
    };
```

```java
Intent intent = new Intent(MainActivity.this, MusicService.class);
bindService(intent, serviceConnection, BIND_AUTO_CREATE);
```

> 使用**绑定服务**时，在MainActivity中创建MusicService对象musicService,通过对象musicService.public方法调用MusicService中的**public**方法

#### 3.SeekBar

| API                    | Explanation                  |
| ---------------------- | ---------------------------- |
| onProgressChanged()    | SeekBar 的进度发生改变时触发 |
| onStartTrackingTouch() | 开始滑动 SeekBar 时触发      |
| onStopTrackingTouch()  | 停止滑动 SeekBar 时触发      |

```java
seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // SeekBar 的进度发生改变时触发的操作
            if (fromUser) {
                timer = new Timer();
                timer.schedule(new ProgressUpdate(), 0, 1000);
                tv_seekBarHint.setText(format(progress));
                musicService.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // 用户开始滑动 SeekBar 时触发的操作
            btn_play.setImageResource(R.drawable.play);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 用户停止滑动 SeekBar 时触发的操作
            btn_play.setImageResource(R.drawable.pause);
            updateSongName(musicService.getCurrentSongName());
            musicService.play();
        }
 });
```

![image-20240505194634590](https://gitee.com/RoseTame/pic/raw/master/blog/202405051946682.png)

#### 4.Timer

```java
private class ProgressUpdate extends TimerTask {
    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long position = musicService.getContentPosition();
                long duration = musicService.getDuration();

                // 更新当前进度文本
                tv_seekBarHint.setText(format(position));
                // 更新歌曲总时长文本
                tv_duration.setText(format(duration));

                // 更新进度条进度
                seekBar.setMax((int) duration);
                seekBar.setProgress((int) position);
            }
        });
    }
}
```

```java
public String format(long position) {
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss"); // "分:秒"格式
    String timeStr = sdf.format(position); //会自动将时长(毫秒数)转换为分秒格式
    return timeStr;
}
```

#### 5. MusicService API

| API                        | **详细代码见代码部分的****MusicService**                              Explanation |
| -------------------------- | ------------------------------------------------------------ |
| init()                     | 初始化播放器                                                 |
| isplaying()                | 判断是否正在播放                                             |
| play()                     | 播放歌曲                                                     |
| pause()                    | 暂停播放                                                     |
| playMusic()                | 播放指定音乐名的歌曲                                         |
| getFileNameFromMediaItem() | 得到音乐文件名                                               |
| playNextTrack()            | 播放下一首                                                   |
| playPreviousTrack()        | 播放上一首                                                   |
| seekTo()                   | 播放歌曲的指定位置                                           |
| getContentPosition()       | 获取当前播放进度                                             |
| getDuration()              | 获取当前播放歌曲总时长                                       |
| getCurrentSongName()       | 获取正在播放的歌曲名                                         |
| getPlayMode()              | 获取当前播放模式                                             |
| setPlayMode()              | 设置播放模式                                                 |

> 因为使用的是绑定Service, MainActivity中定义了MusicService的对象musicService, 所有的API均为public类型。在MainActivity中可通过
>
> **musicService.API**
>
> 进行调用MusicService中的方法。

#### 6. MainActivity API

| API                 | Explanation        |
| ------------------- | ------------------ |
| initView()          | 初始化界面         |
| playOrPauseMusic()  | 播放和暂停音乐     |
| playPreviousTrack() | 前一首             |
| playNextTrack()     | 后一首             |
| changePlayMode()    | 切换播放模式       |
| updateSongName      | 更新当前播放音乐名 |

MainActivity中的API大多调用MusicService当中的API,再进行一些UI界面控制，例如歌曲名的更新，SeekBar的同步，ImageButton的更换等。        

>         1.    播放和暂停ImageButton的切换   
>         1.    顺序播放和单曲循环ImageButton的切换        

  详细代码见代码部分的MainActivity部分。

### 7. 总结

![image-20240505194952331](https://gitee.com/RoseTame/pic/raw/master/blog/202405051949402.png)

> 1. 先进行界面设计，也就是写activity_main.xml; 
>
> 2. 再到MainActivity中搭建框架，写好视图监听操作;
> 3. 然后新建MusicService, 并写好音乐播放所需的API;
> 4. 最后回到MainActivity通过调用MusicService中API补全代码。
