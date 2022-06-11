# wiki_count

## Overview

本项目是对《七天七并发模型》一书中wikipage网页高频词统计项目的复现和优化。主要展示了以下技术：

- 线程池技术
- 锁模型
- lock striping

## Content

本项目按照以下结构组织：

- WordCount: 串行模型
- WorkCountProducerConsumer: 消费者生产者模型
- WordCountSynchronizedHashMap: 使用全局锁的多消费者模型
- WordCountConcurrentHashMap：基于lock striping使用ConcurrentHashMap的多消费者模型
- WordCountBatchConcurrentHashMap: 使用局部HashMap 的多消费者模型
- WordCountSeperateMerge：基于独立合并线程的多消费者模型

以上六种方案中，前五种方法是原书提供的方法，本项目对其进行了两方面的修改：

- 剔除了统计结果中的介词，连词等stop words
- 解决了xml中页面数量小于设定数量时出现异常停止的bug
- 添加了统计结果打印函数

以上方案中，最后一种方案为独创方案，在相同的网页数据上取得了较好的加速度比。

## 实验结果

见下图

![Result 1](https://raw.githubusercontent.com/kylewang1991/wiki_count/main/image/result_1.jpg)

![Result 2](https://raw.githubusercontent.com/kylewang1991/wiki_count/main/image/result_2.jpg)

以上加速度比的base line为串行模型结果。

最终统计结果：

![Final result](https://raw.githubusercontent.com/kylewang1991/wiki_count/main/image/final_result.jpg)