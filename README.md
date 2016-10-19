#基于Thrift的RPC框架

[![Build Status](https://api.travis-ci.org/sofn/trpc.svg)](https://travis-ci.org/sofn/trpc)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

####简介
---------
Thrift是Facebook开源的优秀RPC框架，支持多语言，并且有很好的压缩比和压缩效率，是跨系统调用的利器。但是Thrift只提供了点对点的连接，在构建大规模微服务的时候，手动维护机器列表显然不可能。

######trpc扩展的Thrift功能：
* 服务端Netty发布支持
* 客户端连接池支持
* 异步客户端支持
* 重试机制

######trpc在Thrift的基础上提供的功能：
* 服务发现
* 服务治理
* 负载均衡
* 断路保护
