## Android 小项目 音乐播放器 MusicPlayer

### 一、题目

**音乐播放器：** 

<img src="https://gitee.com/RoseTame/pic/raw/master/blog/202405022325983.jpg" >

**主要要求：** 界面模仿上图 

◼ 音乐文件：放在项目 assets/music 目录下 

◼ 主窗口 Activity+播放列表 ListView+音乐进度 SeekBar+播放 ExoPlayer+后台 Service 

◼ 只需完成的功能：播放/暂停、上一首、下一首(自动下一首)、单曲循环、播放列表选择

**项目地址：** https://github.com/RoseTame/MusicPlayer 

### 二、 界面展示

<img src="https://gitee.com/RoseTame/pic/raw/master/blog/202405031555785.png" width="30%">

> 1. ActionBar
> 2. Imageview表示歌曲图片
> 3. 3个TextView表示歌名、歌手名和歌词，其中歌名会随着歌曲变化而变化
> 4. 6个ImageButton（不实现功能），5个ImageButton（实现功能）
> 5. Seekbar+TextView表示歌曲进度

### 三、Prepare

#### 1. 新建项目MusicPlayer
