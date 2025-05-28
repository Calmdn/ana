---
title: 大数据
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# 大数据

Base URLs:

# Authentication

# KpiController

## GET 健康检查接口

GET /api/kpi/health

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取指定城市的今日KPI

GET /api/kpi/today/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "hour": 0,
      "totalOrders": 0,
      "activeCouriers": 0,
      "coverageAois": 0,
      "ordersPerCourier": 0,
      "ordersPerAoi": 0,
      "efficiencyScore": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListKpiDataDTO](#schemasimpleresponselistkpidatadto)|

## GET 获取指定日期的KPI数据

GET /api/kpi/date/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "hour": 0,
      "totalOrders": 0,
      "activeCouriers": 0,
      "coverageAois": 0,
      "ordersPerCourier": 0,
      "ordersPerAoi": 0,
      "efficiencyScore": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListKpiDataDTO](#schemasimpleresponselistkpidatadto)|

## GET 获取最近几天的KPI数据

GET /api/kpi/recent/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|days|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "hour": 0,
      "totalOrders": 0,
      "activeCouriers": 0,
      "coverageAois": 0,
      "ordersPerCourier": 0,
      "ordersPerAoi": 0,
      "efficiencyScore": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListKpiDataDTO](#schemasimpleresponselistkpidatadto)|

## DELETE 清理旧数据

DELETE /api/kpi/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cutoffDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 统计记录数

GET /api/kpi/count/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

# AnomalyAlertController

## GET 根据ID获取告警详情

GET /api/alerts/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "id": 0,
    "anomalyType": "",
    "city": "",
    "orderId": "",
    "courierId": "",
    "anomalySeverity": "",
    "anomalyValue": 0,
    "thresholdValue": 0,
    "description": "",
    "originalTime": "",
    "analysisDate": "",
    "analysisHour": 0,
    "isResolved": false,
    "createdAt": "",
    "resolvedAt": ""
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseAnomalyAlertDTO](#schemasimpleresponseanomalyalertdto)|

## PUT 更新告警描述

PUT /api/alerts/{id}/description

> Body 请求参数

```json
"string"
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|body|body|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 更新告警严重程度

PUT /api/alerts/{id}/severity

> Body 请求参数

```json
"string"
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|body|body|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取指定城市的未解决告警

GET /api/alerts/unresolved/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 按严重程度获取告警

GET /api/alerts/severity/{severity}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|severity|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 按异常类型获取告警

GET /api/alerts/type/{anomalyType}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|anomalyType|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 按时间范围获取告警

GET /api/alerts/range/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 按配送员获取告警

GET /api/alerts/courier/{courierId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|courierId|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 获取今日告警

GET /api/alerts/today/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 获取高风险告警

GET /api/alerts/high-risk/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 获取订单相关告警

GET /api/alerts/order/{orderId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|orderId|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 获取最近的告警

GET /api/alerts/recent

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## GET 获取偏差最大的告警

GET /api/alerts/highest-deviation

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "anomalyType": "",
      "city": "",
      "orderId": "",
      "courierId": "",
      "anomalySeverity": "",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "",
      "originalTime": "",
      "analysisDate": "",
      "analysisHour": 0,
      "isResolved": false,
      "createdAt": "",
      "resolvedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListAnomalyAlertDTO](#schemasimpleresponselistanomalyalertdto)|

## PUT 解决告警

PUT /api/alerts/resolve/{alertId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|alertId|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 批量解决告警

PUT /api/alerts/resolve/batch

> Body 请求参数

```json
[
  0
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|array[integer]| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取告警数量统计

GET /api/alerts/count

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 否 |none|
|anomalyType|query|string| 否 |none|
|severity|query|string| 否 |none|
|startDate|query|string| 否 |none|
|endDate|query|string| 否 |none|
|isResolved|query|boolean| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

## GET 获取未解决告警数量

GET /api/alerts/count/unresolved

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

## GET 获取告警统计分析

GET /api/alerts/stats

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|
|groupBy|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取今日告警统计

GET /api/alerts/stats/today

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取城市告警趋势

GET /api/alerts/trend/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取小时级告警分布

GET /api/alerts/distribution/hourly/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取配送员告警排行

GET /api/alerts/ranking/courier/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取告警统计信息（综合统计）

GET /api/alerts/statistics/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## DELETE 清理旧告警数据

DELETE /api/alerts/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|daysToKeep|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

# SparkJobLogsController

## POST 保存作业日志

POST /api/spark-jobs

> Body 请求参数

```json
{
  "id": 0,
  "jobName": "string",
  "startTime": "string",
  "endTime": "string",
  "status": "string",
  "inputDeliverPath": "string",
  "inputPickupPath": "string",
  "outputPath": "string",
  "processedRecords": 0,
  "errorMessage": "string",
  "executionTimeSeconds": 0,
  "timeFormatUsed": "string",
  "defaultYear": 0,
  "createdAt": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SparkJobLogs](#schemasparkjoblogs)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## POST 批量保存作业日志

POST /api/spark-jobs/batch

> Body 请求参数

```json
[
  {
    "id": 0,
    "jobName": "string",
    "startTime": "string",
    "endTime": "string",
    "status": "string",
    "inputDeliverPath": "string",
    "inputPickupPath": "string",
    "outputPath": "string",
    "processedRecords": 0,
    "errorMessage": "string",
    "executionTimeSeconds": 0,
    "timeFormatUsed": "string",
    "defaultYear": 0,
    "createdAt": "string"
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SparkJobLogs](#schemasparkjoblogs)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取最近的作业日志

GET /api/spark-jobs/recent

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 获取失败的作业

GET /api/spark-jobs/failed

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 获取运行中的作业

GET /api/spark-jobs/running

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 获取成功的作业

GET /api/spark-jobs/successful

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 按作业名称获取日志

GET /api/spark-jobs/by-name/{jobName}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|jobName|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 按状态获取作业日志

GET /api/spark-jobs/by-status/{status}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|status|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 按时间范围获取作业日志

GET /api/spark-jobs/by-time-range

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|startTime|query|string| 是 |none|
|endTime|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 获取今日作业日志

GET /api/spark-jobs/today

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 获取长时间运行的作业

GET /api/spark-jobs/long-running

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|thresholdMinutes|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 获取有错误的作业

GET /api/spark-jobs/errors

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "id": 0,
      "jobName": "",
      "startTime": "",
      "endTime": "",
      "status": "",
      "inputDeliverPath": "",
      "inputPickupPath": "",
      "outputPath": "",
      "processedRecords": 0,
      "errorMessage": "",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "",
      "defaultYear": 0,
      "createdAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSparkJobLogsDTO](#schemasimpleresponselistsparkjoblogsdto)|

## GET 根据ID获取作业详情

GET /api/spark-jobs/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "id": 0,
    "jobName": "",
    "startTime": "",
    "endTime": "",
    "status": "",
    "inputDeliverPath": "",
    "inputPickupPath": "",
    "outputPath": "",
    "processedRecords": 0,
    "errorMessage": "",
    "executionTimeSeconds": 0,
    "timeFormatUsed": "",
    "defaultYear": 0,
    "createdAt": ""
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseSparkJobLogsDTO](#schemasimpleresponsesparkjoblogsdto)|

## GET 获取作业统计信息

GET /api/spark-jobs/statistics

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "": {}
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseMapObject](#schemasimpleresponsemapobject)|

## GET 获取作业执行趋势

GET /api/spark-jobs/trend

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|days|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## PUT 更新作业状态

PUT /api/spark-jobs/{id}/status

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|status|query|string| 是 |none|
|endTime|query|string| 否 |none|
|executionTimeSeconds|query|integer| 否 |none|
|errorMessage|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## DELETE 清理旧的作业日志

DELETE /api/spark-jobs/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|daysToKeep|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 系统健康检查

GET /api/spark-jobs/health

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

# TimeEfficiencyController

## POST 保存时间效率数据

POST /api/time-efficiency

> Body 请求参数

```json
{
  "city": "string",
  "date": "string",
  "hour": 0,
  "totalDeliveries": 0,
  "avgDeliveryTime": 0,
  "medianDeliveryTime": 0,
  "p95DeliveryTime": 0,
  "fastDeliveries": 0,
  "normalDeliveries": 0,
  "slowDeliveries": 0,
  "fastDeliveryRate": 0,
  "slowDeliveryRate": 0,
  "totalPickups": 0,
  "avgPickupTime": 0,
  "medianPickupTime": 0,
  "p95PickupTime": 0,
  "fastPickups": 0,
  "normalPickups": 0,
  "slowPickups": 0,
  "onTimePickups": 0,
  "fastPickupRate": 0,
  "slowPickupRate": 0,
  "onTimePickupRate": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[TimeEfficiencyMetrics](#schematimeefficiencymetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 更新时间效率数据

PUT /api/time-efficiency

> Body 请求参数

```json
{
  "city": "string",
  "date": "string",
  "hour": 0,
  "totalDeliveries": 0,
  "avgDeliveryTime": 0,
  "medianDeliveryTime": 0,
  "p95DeliveryTime": 0,
  "fastDeliveries": 0,
  "normalDeliveries": 0,
  "slowDeliveries": 0,
  "fastDeliveryRate": 0,
  "slowDeliveryRate": 0,
  "totalPickups": 0,
  "avgPickupTime": 0,
  "medianPickupTime": 0,
  "p95PickupTime": 0,
  "fastPickups": 0,
  "normalPickups": 0,
  "slowPickups": 0,
  "onTimePickups": 0,
  "fastPickupRate": 0,
  "slowPickupRate": 0,
  "onTimePickupRate": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[TimeEfficiencyMetrics](#schematimeefficiencymetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## POST 批量保存时间效率数据

POST /api/time-efficiency/batch

> Body 请求参数

```json
[
  {
    "city": "string",
    "date": "string",
    "hour": 0,
    "totalDeliveries": 0,
    "avgDeliveryTime": 0,
    "medianDeliveryTime": 0,
    "p95DeliveryTime": 0,
    "fastDeliveries": 0,
    "normalDeliveries": 0,
    "slowDeliveries": 0,
    "fastDeliveryRate": 0,
    "slowDeliveryRate": 0,
    "totalPickups": 0,
    "avgPickupTime": 0,
    "medianPickupTime": 0,
    "p95PickupTime": 0,
    "fastPickups": 0,
    "normalPickups": 0,
    "slowPickups": 0,
    "onTimePickups": 0,
    "fastPickupRate": 0,
    "slowPickupRate": 0,
    "onTimePickupRate": 0
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[TimeEfficiencyMetrics](#schematimeefficiencymetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取今日时间效率

GET /api/time-efficiency/today/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 获取指定日期的时间效率

GET /api/time-efficiency/date/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 获取效率趋势分析

GET /api/time-efficiency/trend/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|days|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 获取效率趋势统计

GET /api/time-efficiency/trend-stats/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取指定时间范围的时间效率

GET /api/time-efficiency/range/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 多条件搜索时间效率

GET /api/time-efficiency/search

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 否 |none|
|startDate|query|string| 否 |none|
|endDate|query|string| 否 |none|
|minFastRate|query|number| 否 |none|
|maxSlowRate|query|number| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 获取效率分布统计

GET /api/time-efficiency/distribution/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取时间效率排行

GET /api/time-efficiency/ranking

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cities|query|array[string]| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取慢配送分析

GET /api/time-efficiency/slow-delivery/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|threshold|query|number| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 获取快速配送分析

GET /api/time-efficiency/fast-delivery/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|threshold|query|number| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListTimeEfficiencyDTO](#schemasimpleresponselisttimeefficiencydto)|

## GET 获取时间效率汇总统计

GET /api/time-efficiency/summary/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "": {}
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseMapObject](#schemasimpleresponsemapobject)|

## GET 获取最新时间效率数据

GET /api/time-efficiency/latest/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "city": "",
    "date": "",
    "totalDeliveries": 0,
    "avgDeliveryTime": 0,
    "fastDeliveries": 0,
    "slowDeliveries": 0,
    "fastDeliveryRate": 0,
    "slowDeliveryRate": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseTimeEfficiencyDTO](#schemasimpleresponsetimeefficiencydto)|

## GET 获取城市间时间效率对比

GET /api/time-efficiency/comparison

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cities|query|array[string]| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## DELETE 清理旧数据

DELETE /api/time-efficiency/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cutoffDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 统计记录数

GET /api/time-efficiency/count/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

# SpatialAnalysisController

## POST 保存空间分析数据

POST /api/spatial-analysis

> Body 请求参数

```json
{
  "city": "string",
  "date": "string",
  "lngGrid": 0,
  "latGrid": 0,
  "deliveryCount": 0,
  "uniqueCouriers": 0,
  "avgDeliveryTime": 0,
  "avgDeliveryDistance": 0,
  "deliveryDensity": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SpatialAnalysisMetrics](#schemaspatialanalysismetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 更新空间分析数据

PUT /api/spatial-analysis

> Body 请求参数

```json
{
  "city": "string",
  "date": "string",
  "lngGrid": 0,
  "latGrid": 0,
  "deliveryCount": 0,
  "uniqueCouriers": 0,
  "avgDeliveryTime": 0,
  "avgDeliveryDistance": 0,
  "deliveryDensity": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SpatialAnalysisMetrics](#schemaspatialanalysismetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## POST 批量保存空间分析数据

POST /api/spatial-analysis/batch

> Body 请求参数

```json
[
  {
    "city": "string",
    "date": "string",
    "lngGrid": 0,
    "latGrid": 0,
    "deliveryCount": 0,
    "uniqueCouriers": 0,
    "avgDeliveryTime": 0,
    "avgDeliveryDistance": 0,
    "deliveryDensity": 0
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SpatialAnalysisMetrics](#schemaspatialanalysismetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取今日空间分析

GET /api/spatial-analysis/today/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSpatialAnalysisDTO](#schemasimpleresponselistspatialanalysisdto)|

## GET 获取指定日期的空间分析

GET /api/spatial-analysis/date/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSpatialAnalysisDTO](#schemasimpleresponselistspatialanalysisdto)|

## GET 获取热点区域分析

GET /api/spatial-analysis/hotspots/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSpatialAnalysisDTO](#schemasimpleresponselistspatialanalysisdto)|

## GET 获取密度分析

GET /api/spatial-analysis/density/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSpatialAnalysisDTO](#schemasimpleresponselistspatialanalysisdto)|

## GET 获取指定时间范围的空间分析

GET /api/spatial-analysis/range/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSpatialAnalysisDTO](#schemasimpleresponselistspatialanalysisdto)|

## GET 根据地理范围获取空间分析

GET /api/spatial-analysis/geo-range/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|minLng|query|number| 是 |none|
|maxLng|query|number| 是 |none|
|minLat|query|number| 是 |none|
|maxLat|query|number| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "date": "",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListSpatialAnalysisDTO](#schemasimpleresponselistspatialanalysisdto)|

## GET 获取配送密度热点

GET /api/spatial-analysis/heatmap/density/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取配送时间热图数据

GET /api/spatial-analysis/heatmap/delivery-time/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取空间分布统计

GET /api/spatial-analysis/stats/distribution/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取网格聚合数据

GET /api/spatial-analysis/grid-aggregation/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 是 |none|
|gridSize|query|number| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取空间汇总统计

GET /api/spatial-analysis/summary/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "": {}
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseMapObject](#schemasimpleresponsemapobject)|

## GET 获取配送员空间分布

GET /api/spatial-analysis/courier-distribution/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取城市间空间对比

GET /api/spatial-analysis/comparison

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cities|query|array[string]| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## DELETE 清理旧数据

DELETE /api/spatial-analysis/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cutoffDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 统计记录数

GET /api/spatial-analysis/count/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

# PredictiveAnalysisController

## POST 保存预测分析数据

POST /api/predictive-analysis

> Body 请求参数

```json
{
  "city": "string",
  "dsDate": "string",
  "hour": 0,
  "orderVolume": 0,
  "courierCount": 0,
  "avgDuration": 0,
  "totalDistance": 0,
  "volumeTrend": 0,
  "efficiencyScore": 0,
  "dataType": "string",
  "regionId": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[PredictiveAnalysisData](#schemapredictiveanalysisdata)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 更新预测分析数据

PUT /api/predictive-analysis

> Body 请求参数

```json
{
  "city": "string",
  "dsDate": "string",
  "hour": 0,
  "orderVolume": 0,
  "courierCount": 0,
  "avgDuration": 0,
  "totalDistance": 0,
  "volumeTrend": 0,
  "efficiencyScore": 0,
  "dataType": "string",
  "regionId": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[PredictiveAnalysisData](#schemapredictiveanalysisdata)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## POST 批量保存预测分析数据

POST /api/predictive-analysis/batch

> Body 请求参数

```json
[
  {
    "city": "string",
    "dsDate": "string",
    "hour": 0,
    "orderVolume": 0,
    "courierCount": 0,
    "avgDuration": 0,
    "totalDistance": 0,
    "volumeTrend": 0,
    "efficiencyScore": 0,
    "dataType": "string",
    "regionId": "string"
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[PredictiveAnalysisData](#schemapredictiveanalysisdata)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取最新预测数据

GET /api/predictive-analysis/latest/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 按类型获取最新预测数据

GET /api/predictive-analysis/latest/{city}/{dataType}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|dataType|path|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 获取历史趋势数据

GET /api/predictive-analysis/trends/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 获取容量分析数据

GET /api/predictive-analysis/capacity/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 获取指定日期的预测数据

GET /api/predictive-analysis/date/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 获取指定类型和时间范围的预测分析

GET /api/predictive-analysis/custom/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|dataType|query|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 多条件搜索预测数据

GET /api/predictive-analysis/search

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 否 |none|
|regionId|query|string| 否 |none|
|dataType|query|string| 否 |none|
|startDate|query|string| 否 |none|
|endDate|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": "",
      "dsDate": "",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListPredictiveAnalysisDTO](#schemasimpleresponselistpredictiveanalysisdto)|

## GET 获取订单量趋势

GET /api/predictive-analysis/trend/order-volume/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|dataType|query|string| 否 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取小时分布分析

GET /api/predictive-analysis/distribution/hourly/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取效率预测趋势

GET /api/predictive-analysis/trend/efficiency/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取容量分析统计

GET /api/predictive-analysis/stats/capacity/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取数据类型统计

GET /api/predictive-analysis/stats/data-type/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取预测汇总统计

GET /api/predictive-analysis/summary/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "": {}
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseMapObject](#schemasimpleresponsemapobject)|

## GET 获取城市间预测对比

GET /api/predictive-analysis/comparison

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cities|query|array[string]| 是 |none|
|dataType|query|string| 否 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## DELETE 清理旧数据

DELETE /api/predictive-analysis/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cutoffDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 统计记录数

GET /api/predictive-analysis/count/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|dataType|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

# ComprehensiveReportController

## POST 创建或更新报告

POST /api/comprehensive-reports

> Body 请求参数

```json
{
  "city": "string",
  "regionId": 0,
  "date": "string",
  "totalDeliveries": 0,
  "activeCouriers": 0,
  "servedAois": 0,
  "avgDeliveryTime": 0,
  "totalDistance": 0,
  "fastDeliveries": 0,
  "avgOrdersPerCourier": 0,
  "avgDistancePerOrder": 0,
  "fastDeliveryRate": 0,
  "reportType": "string",
  "generatedAt": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ComprehensiveReportDTO](#schemacomprehensivereportdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 更新报告

PUT /api/comprehensive-reports

> Body 请求参数

```json
{
  "city": "string",
  "regionId": 0,
  "date": "string",
  "totalDeliveries": 0,
  "activeCouriers": 0,
  "servedAois": 0,
  "avgDeliveryTime": 0,
  "totalDistance": 0,
  "fastDeliveries": 0,
  "avgOrdersPerCourier": 0,
  "avgDistancePerOrder": 0,
  "fastDeliveryRate": 0,
  "reportType": "string",
  "generatedAt": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ComprehensiveReportDTO](#schemacomprehensivereportdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 根据城市、日期和类型获取特定报告

GET /api/comprehensive-reports/specific

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 是 |none|
|date|query|string| 是 |none|
|reportType|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "city": "",
    "regionId": 0,
    "date": "",
    "totalDeliveries": 0,
    "activeCouriers": 0,
    "servedAois": 0,
    "avgDeliveryTime": 0,
    "totalDistance": 0,
    "fastDeliveries": 0,
    "avgOrdersPerCourier": 0,
    "avgDistancePerOrder": 0,
    "fastDeliveryRate": 0,
    "reportType": "",
    "generatedAt": ""
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseComprehensiveReportDTO](#schemasimpleresponsecomprehensivereportdto)|

## GET 获取最新日报

GET /api/comprehensive-reports/daily/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "date": "",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "",
      "generatedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListComprehensiveReportDTO](#schemasimpleresponselistcomprehensivereportdto)|

## GET 获取最新周报

GET /api/comprehensive-reports/weekly/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "date": "",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "",
      "generatedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListComprehensiveReportDTO](#schemasimpleresponselistcomprehensivereportdto)|

## GET 获取指定时间范围的报告

GET /api/comprehensive-reports/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|reportType|query|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "date": "",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "",
      "generatedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListComprehensiveReportDTO](#schemasimpleresponselistcomprehensivereportdto)|

## GET 根据城市和日期范围获取报告（不限类型）

GET /api/comprehensive-reports/range/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "date": "",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "",
      "generatedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListComprehensiveReportDTO](#schemasimpleresponselistcomprehensivereportdto)|

## GET 根据报告类型获取报告

GET /api/comprehensive-reports/type/{reportType}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|reportType|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "date": "",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "",
      "generatedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListComprehensiveReportDTO](#schemasimpleresponselistcomprehensivereportdto)|

## GET 获取城市最新报告

GET /api/comprehensive-reports/latest/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "city": "",
    "regionId": 0,
    "date": "",
    "totalDeliveries": 0,
    "activeCouriers": 0,
    "servedAois": 0,
    "avgDeliveryTime": 0,
    "totalDistance": 0,
    "fastDeliveries": 0,
    "avgOrdersPerCourier": 0,
    "avgDistancePerOrder": 0,
    "fastDeliveryRate": 0,
    "reportType": "",
    "generatedAt": ""
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseComprehensiveReportDTO](#schemasimpleresponsecomprehensivereportdto)|

## GET 获取所有城市列表

GET /api/comprehensive-reports/cities

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    ""
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListString](#schemasimpleresponseliststring)|

## GET 根据日期获取所有城市报告

GET /api/comprehensive-reports/date/{date}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|date|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "date": "",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "",
      "generatedAt": ""
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListComprehensiveReportDTO](#schemasimpleresponselistcomprehensivereportdto)|

## GET 获取城市报告趋势

GET /api/comprehensive-reports/trend/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取城市效率排行

GET /api/comprehensive-reports/ranking/efficiency

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 统计指定时间范围内的总配送量

GET /api/comprehensive-reports/stats/deliveries/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseLong](#schemasimpleresponselong)|

## GET 获取配送效率统计

GET /api/comprehensive-reports/stats/efficiency

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 统计报告数量

GET /api/comprehensive-reports/count

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 是 |none|
|reportType|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

## DELETE 清理旧报告数据

DELETE /api/comprehensive-reports/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|daysToKeep|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

# CostAnalysisMetricsController

## POST 保存成本分析数据

POST /api/cost-analysis

> Body 请求参数

```json
{
  "city": "string",
  "regionId": 0,
  "date": "string",
  "totalCost": 0,
  "totalFuelCost": 0,
  "totalTimeCost": 0,
  "totalOrders": 0,
  "totalDistance": 0,
  "costPerOrder": 0,
  "costPerKm": 0,
  "fuelCostRatio": 0,
  "analysisType": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[CostAnalysisMetrics](#schemacostanalysismetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## PUT 更新成本分析数据

PUT /api/cost-analysis

> Body 请求参数

```json
{
  "city": "string",
  "regionId": 0,
  "date": "string",
  "totalCost": 0,
  "totalFuelCost": 0,
  "totalTimeCost": 0,
  "totalOrders": 0,
  "totalDistance": 0,
  "costPerOrder": 0,
  "costPerKm": 0,
  "fuelCostRatio": 0,
  "analysisType": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[CostAnalysisMetrics](#schemacostanalysismetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## POST 批量保存成本分析数据

POST /api/cost-analysis/batch

> Body 请求参数

```json
[
  {
    "city": "string",
    "regionId": 0,
    "date": "string",
    "totalCost": 0,
    "totalFuelCost": 0,
    "totalTimeCost": 0,
    "totalOrders": 0,
    "totalDistance": 0,
    "costPerOrder": 0,
    "costPerKm": 0,
    "fuelCostRatio": 0,
    "analysisType": "string"
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[CostAnalysisMetrics](#schemacostanalysismetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 根据城市查询成本分析

GET /api/cost-analysis/city/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 根据区域查询成本分析

GET /api/cost-analysis/region/{regionId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|regionId|path|integer| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 多条件查询成本分析

GET /api/cost-analysis/search

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 否 |none|
|regionId|query|integer| 否 |none|
|analysisType|query|string| 否 |none|
|startDate|query|string| 否 |none|
|endDate|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 获取城市成本趋势

GET /api/cost-analysis/trend/city/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 获取区域成本排行

GET /api/cost-analysis/ranking/region

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 获取分析类型统计

GET /api/cost-analysis/stats/analysis-type

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 获取高成本告警

GET /api/cost-analysis/alerts/high-cost

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|threshold|query|number| 是 |none|
|date|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 获取成本汇总统计

GET /api/cost-analysis/summary/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## DELETE 清理旧数据

DELETE /api/cost-analysis/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cutoffDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

## GET 统计城市成本分析数量

GET /api/cost-analysis/count/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityMapObject](#schemaresponseentitymapobject)|

# OperationalEfficiencyController

## POST 保存运营效率数据

POST /api/operational-efficiency

> Body 请求参数

```json
{
  "city": "string",
  "regionId": 0,
  "courierId": 0,
  "date": "string",
  "totalOrders": 0,
  "uniqueAoiServed": 0,
  "totalDistance": 0,
  "totalWorkingHours": 0,
  "avgDeliveryTime": 0,
  "ordersPerHour": 0,
  "distancePerOrder": 0,
  "efficiencyScore": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[OperationalEfficiencyMetrics](#schemaoperationalefficiencymetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## PUT 更新运营效率数据

PUT /api/operational-efficiency

> Body 请求参数

```json
{
  "city": "string",
  "regionId": 0,
  "courierId": 0,
  "date": "string",
  "totalOrders": 0,
  "uniqueAoiServed": 0,
  "totalDistance": 0,
  "totalWorkingHours": 0,
  "avgDeliveryTime": 0,
  "ordersPerHour": 0,
  "distancePerOrder": 0,
  "efficiencyScore": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[OperationalEfficiencyMetrics](#schemaoperationalefficiencymetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## POST 批量保存运营效率数据

POST /api/operational-efficiency/batch

> Body 请求参数

```json
[
  {
    "city": "string",
    "regionId": 0,
    "courierId": 0,
    "date": "string",
    "totalOrders": 0,
    "uniqueAoiServed": 0,
    "totalDistance": 0,
    "totalWorkingHours": 0,
    "avgDeliveryTime": 0,
    "ordersPerHour": 0,
    "distancePerOrder": 0,
    "efficiencyScore": 0
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[OperationalEfficiencyMetrics](#schemaoperationalefficiencymetrics)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 获取今日运营效率

GET /api/operational-efficiency/today/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取指定日期的效率数据

GET /api/operational-efficiency/date/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|date|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取指定时间范围的运营效率

GET /api/operational-efficiency/range/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取配送员效率数据

GET /api/operational-efficiency/courier/{courierId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|courierId|path|integer| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取区域效率数据

GET /api/operational-efficiency/region/{regionId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|regionId|path|integer| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 多条件查询效率数据

GET /api/operational-efficiency/search

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 否 |none|
|regionId|query|integer| 否 |none|
|courierId|query|integer| 否 |none|
|startDate|query|string| 否 |none|
|endDate|query|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取城市效率趋势

GET /api/operational-efficiency/trend/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取配送员效率排行

GET /api/operational-efficiency/ranking/courier

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取区域效率排行

GET /api/operational-efficiency/ranking/region

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|query|string| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取效率分布统计

GET /api/operational-efficiency/distribution/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## GET 获取低效率告警

GET /api/operational-efficiency/alerts/low-efficiency

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|threshold|query|number| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取高效率表现

GET /api/operational-efficiency/performance/high-efficiency

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|threshold|query|number| 是 |none|
|startDate|query|string| 是 |none|
|limit|query|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "city": "",
      "regionId": 0,
      "courierId": 0,
      "date": "",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "",
      "workloadLevel": "",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListOperationalEfficiencyDTO](#schemasimpleresponselistoperationalefficiencydto)|

## GET 获取运营效率汇总统计

GET /api/operational-efficiency/summary/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|
|startDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "": {}
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseMapObject](#schemasimpleresponsemapobject)|

## GET 获取最新运营效率数据

GET /api/operational-efficiency/latest/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "city": "",
    "regionId": 0,
    "courierId": 0,
    "date": "",
    "totalOrders": 0,
    "uniqueAoiServed": 0,
    "totalDistance": 0,
    "totalWorkingHours": 0,
    "avgDeliveryTime": 0,
    "ordersPerHour": 0,
    "distancePerOrder": 0,
    "efficiencyScore": 0,
    "efficiencyLevel": "",
    "workloadLevel": "",
    "aoiCoverageRate": 0,
    "distanceEfficiency": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseOperationalEfficiencyDTO](#schemasimpleresponseoperationalefficiencydto)|

## GET 获取配送员最新效率数据

GET /api/operational-efficiency/latest/courier/{courierId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|courierId|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": {
    "city": "",
    "regionId": 0,
    "courierId": 0,
    "date": "",
    "totalOrders": 0,
    "uniqueAoiServed": 0,
    "totalDistance": 0,
    "totalWorkingHours": 0,
    "avgDeliveryTime": 0,
    "ordersPerHour": 0,
    "distancePerOrder": 0,
    "efficiencyScore": 0,
    "efficiencyLevel": "",
    "workloadLevel": "",
    "aoiCoverageRate": 0,
    "distanceEfficiency": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseOperationalEfficiencyDTO](#schemasimpleresponseoperationalefficiencydto)|

## GET 获取城市间效率对比

GET /api/operational-efficiency/comparison

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cities|query|array[string]| 是 |none|
|startDate|query|string| 是 |none|
|endDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": [
    {
      "": {}
    }
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseListMapObject](#schemasimpleresponselistmapobject)|

## DELETE 清理旧数据

DELETE /api/operational-efficiency/cleanup

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|cutoffDate|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": ""
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseString](#schemasimpleresponsestring)|

## GET 统计城市效率记录数

GET /api/operational-efficiency/count/{city}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|city|path|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

## GET 统计配送员记录数

GET /api/operational-efficiency/count/courier/{courierId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|courierId|path|integer| 是 |none|

> 返回示例

> 200 Response

```json
{
  "success": false,
  "message": "",
  "data": 0
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SimpleResponseInteger](#schemasimpleresponseinteger)|

# 数据模型

<h2 id="tocS_SimpleResponseString">SimpleResponseString</h2>

<a id="schemasimpleresponsestring"></a>
<a id="schema_SimpleResponseString"></a>
<a id="tocSsimpleresponsestring"></a>
<a id="tocssimpleresponsestring"></a>

```json
{
  "success": true,
  "message": "string",
  "data": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|string|false|none||none|

<h2 id="tocS_KpiDataDTO">KpiDataDTO</h2>

<a id="schemakpidatadto"></a>
<a id="schema_KpiDataDTO"></a>
<a id="tocSkpidatadto"></a>
<a id="tocskpidatadto"></a>

```json
{
  "city": "string",
  "date": "string",
  "hour": 0,
  "totalOrders": 0,
  "activeCouriers": 0,
  "coverageAois": 0,
  "ordersPerCourier": 0,
  "ordersPerAoi": 0,
  "efficiencyScore": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|date|string|false|none||none|
|hour|integer|false|none||none|
|totalOrders|integer(int64)|false|none||none|
|activeCouriers|integer|false|none||none|
|coverageAois|integer|false|none||none|
|ordersPerCourier|number|false|none||none|
|ordersPerAoi|number|false|none||none|
|efficiencyScore|number|false|none||none|

<h2 id="tocS_SimpleResponseListKpiDataDTO">SimpleResponseListKpiDataDTO</h2>

<a id="schemasimpleresponselistkpidatadto"></a>
<a id="schema_SimpleResponseListKpiDataDTO"></a>
<a id="tocSsimpleresponselistkpidatadto"></a>
<a id="tocssimpleresponselistkpidatadto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "date": "string",
      "hour": 0,
      "totalOrders": 0,
      "activeCouriers": 0,
      "coverageAois": 0,
      "ordersPerCourier": 0,
      "ordersPerAoi": 0,
      "efficiencyScore": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[KpiDataDTO](#schemakpidatadto)]|false|none||none|

<h2 id="tocS_AnomalyAlertDTO">AnomalyAlertDTO</h2>

<a id="schemaanomalyalertdto"></a>
<a id="schema_AnomalyAlertDTO"></a>
<a id="tocSanomalyalertdto"></a>
<a id="tocsanomalyalertdto"></a>

```json
{
  "id": 0,
  "anomalyType": "string",
  "city": "string",
  "orderId": "string",
  "courierId": "string",
  "anomalySeverity": "string",
  "anomalyValue": 0,
  "thresholdValue": 0,
  "description": "string",
  "originalTime": "string",
  "analysisDate": "string",
  "analysisHour": 0,
  "isResolved": true,
  "createdAt": "string",
  "resolvedAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|anomalyType|string|false|none||none|
|city|string|false|none||none|
|orderId|string|false|none||none|
|courierId|string|false|none||none|
|anomalySeverity|string|false|none||none|
|anomalyValue|number|false|none||none|
|thresholdValue|number|false|none||none|
|description|string|false|none||none|
|originalTime|string|false|none||none|
|analysisDate|string|false|none||none|
|analysisHour|integer|false|none||none|
|isResolved|boolean|false|none||none|
|createdAt|string|false|none||none|
|resolvedAt|string|false|none||none|

<h2 id="tocS_SimpleResponseInteger">SimpleResponseInteger</h2>

<a id="schemasimpleresponseinteger"></a>
<a id="schema_SimpleResponseInteger"></a>
<a id="tocSsimpleresponseinteger"></a>
<a id="tocssimpleresponseinteger"></a>

```json
{
  "success": true,
  "message": "string",
  "data": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|integer|false|none||none|

<h2 id="tocS_SimpleResponseListAnomalyAlertDTO">SimpleResponseListAnomalyAlertDTO</h2>

<a id="schemasimpleresponselistanomalyalertdto"></a>
<a id="schema_SimpleResponseListAnomalyAlertDTO"></a>
<a id="tocSsimpleresponselistanomalyalertdto"></a>
<a id="tocssimpleresponselistanomalyalertdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "id": 0,
      "anomalyType": "string",
      "city": "string",
      "orderId": "string",
      "courierId": "string",
      "anomalySeverity": "string",
      "anomalyValue": 0,
      "thresholdValue": 0,
      "description": "string",
      "originalTime": "string",
      "analysisDate": "string",
      "analysisHour": 0,
      "isResolved": true,
      "createdAt": "string",
      "resolvedAt": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[AnomalyAlertDTO](#schemaanomalyalertdto)]|false|none||none|

<h2 id="tocS_CostAnalysisDTO">CostAnalysisDTO</h2>

<a id="schemacostanalysisdto"></a>
<a id="schema_CostAnalysisDTO"></a>
<a id="tocScostanalysisdto"></a>
<a id="tocscostanalysisdto"></a>

```json
{
  "city": "string",
  "regionId": "string",
  "courierId": "string",
  "analysisDate": "string",
  "totalCost": 0,
  "totalFuelCost": 0,
  "totalTimeCost": 0,
  "totalOrders": 0,
  "totalDistance": 0,
  "costPerOrder": 0,
  "costPerKm": 0,
  "fuelCostRatio": 0,
  "workingHours": 0,
  "productivityScore": 0,
  "efficiencyRating": "string",
  "analysisType": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|regionId|string|false|none||none|
|courierId|string|false|none||none|
|analysisDate|string|false|none||none|
|totalCost|number|false|none||none|
|totalFuelCost|number|false|none||none|
|totalTimeCost|number|false|none||none|
|totalOrders|integer|false|none||none|
|totalDistance|number|false|none||none|
|costPerOrder|number|false|none||none|
|costPerKm|number|false|none||none|
|fuelCostRatio|number|false|none||none|
|workingHours|number|false|none||none|
|productivityScore|number|false|none||none|
|efficiencyRating|string|false|none||none|
|analysisType|string|false|none||none|

<h2 id="tocS_SimpleResponseAnomalyAlertDTO">SimpleResponseAnomalyAlertDTO</h2>

<a id="schemasimpleresponseanomalyalertdto"></a>
<a id="schema_SimpleResponseAnomalyAlertDTO"></a>
<a id="tocSsimpleresponseanomalyalertdto"></a>
<a id="tocssimpleresponseanomalyalertdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": {
    "id": 0,
    "anomalyType": "string",
    "city": "string",
    "orderId": "string",
    "courierId": "string",
    "anomalySeverity": "string",
    "anomalyValue": 0,
    "thresholdValue": 0,
    "description": "string",
    "originalTime": "string",
    "analysisDate": "string",
    "analysisHour": 0,
    "isResolved": true,
    "createdAt": "string",
    "resolvedAt": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[AnomalyAlertDTO](#schemaanomalyalertdto)|false|none||com.logistics.service.dto.AnomalyAlertDTO|

<h2 id="tocS_SimpleResponseListCostAnalysisDTO">SimpleResponseListCostAnalysisDTO</h2>

<a id="schemasimpleresponselistcostanalysisdto"></a>
<a id="schema_SimpleResponseListCostAnalysisDTO"></a>
<a id="tocSsimpleresponselistcostanalysisdto"></a>
<a id="tocssimpleresponselistcostanalysisdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "regionId": "string",
      "courierId": "string",
      "analysisDate": "string",
      "totalCost": 0,
      "totalFuelCost": 0,
      "totalTimeCost": 0,
      "totalOrders": 0,
      "totalDistance": 0,
      "costPerOrder": 0,
      "costPerKm": 0,
      "fuelCostRatio": 0,
      "workingHours": 0,
      "productivityScore": 0,
      "efficiencyRating": "string",
      "analysisType": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[CostAnalysisDTO](#schemacostanalysisdto)]|false|none||none|

<h2 id="tocS_SparkJobLogsDTO">SparkJobLogsDTO</h2>

<a id="schemasparkjoblogsdto"></a>
<a id="schema_SparkJobLogsDTO"></a>
<a id="tocSsparkjoblogsdto"></a>
<a id="tocssparkjoblogsdto"></a>

```json
{
  "id": 0,
  "jobName": "string",
  "startTime": "string",
  "endTime": "string",
  "status": "string",
  "inputDeliverPath": "string",
  "inputPickupPath": "string",
  "outputPath": "string",
  "processedRecords": 0,
  "errorMessage": "string",
  "executionTimeSeconds": 0,
  "timeFormatUsed": "string",
  "defaultYear": 0,
  "createdAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|jobName|string|false|none||none|
|startTime|string|false|none||none|
|endTime|string|false|none||none|
|status|string|false|none||none|
|inputDeliverPath|string|false|none||none|
|inputPickupPath|string|false|none||none|
|outputPath|string|false|none||none|
|processedRecords|integer(int64)|false|none||none|
|errorMessage|string|false|none||none|
|executionTimeSeconds|integer|false|none||none|
|timeFormatUsed|string|false|none||none|
|defaultYear|integer|false|none||none|
|createdAt|string|false|none||none|

<h2 id="tocS_SimpleResponseListSparkJobLogsDTO">SimpleResponseListSparkJobLogsDTO</h2>

<a id="schemasimpleresponselistsparkjoblogsdto"></a>
<a id="schema_SimpleResponseListSparkJobLogsDTO"></a>
<a id="tocSsimpleresponselistsparkjoblogsdto"></a>
<a id="tocssimpleresponselistsparkjoblogsdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "id": 0,
      "jobName": "string",
      "startTime": "string",
      "endTime": "string",
      "status": "string",
      "inputDeliverPath": "string",
      "inputPickupPath": "string",
      "outputPath": "string",
      "processedRecords": 0,
      "errorMessage": "string",
      "executionTimeSeconds": 0,
      "timeFormatUsed": "string",
      "defaultYear": 0,
      "createdAt": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[SparkJobLogsDTO](#schemasparkjoblogsdto)]|false|none||none|

<h2 id="tocS_SimpleResponseSparkJobLogsDTO">SimpleResponseSparkJobLogsDTO</h2>

<a id="schemasimpleresponsesparkjoblogsdto"></a>
<a id="schema_SimpleResponseSparkJobLogsDTO"></a>
<a id="tocSsimpleresponsesparkjoblogsdto"></a>
<a id="tocssimpleresponsesparkjoblogsdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": {
    "id": 0,
    "jobName": "string",
    "startTime": "string",
    "endTime": "string",
    "status": "string",
    "inputDeliverPath": "string",
    "inputPickupPath": "string",
    "outputPath": "string",
    "processedRecords": 0,
    "errorMessage": "string",
    "executionTimeSeconds": 0,
    "timeFormatUsed": "string",
    "defaultYear": 0,
    "createdAt": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[SparkJobLogsDTO](#schemasparkjoblogsdto)|false|none||com.logistics.service.dto.SparkJobLogsDTO|

<h2 id="tocS_key">key</h2>

<a id="schemakey"></a>
<a id="schema_key"></a>
<a id="tocSkey"></a>
<a id="tocskey"></a>

```json
{}

```

### 属性

*None*

<h2 id="tocS_SparkJobLogs">SparkJobLogs</h2>

<a id="schemasparkjoblogs"></a>
<a id="schema_SparkJobLogs"></a>
<a id="tocSsparkjoblogs"></a>
<a id="tocssparkjoblogs"></a>

```json
{
  "id": 0,
  "jobName": "string",
  "startTime": "string",
  "endTime": "string",
  "status": "string",
  "inputDeliverPath": "string",
  "inputPickupPath": "string",
  "outputPath": "string",
  "processedRecords": 0,
  "errorMessage": "string",
  "executionTimeSeconds": 0,
  "timeFormatUsed": "string",
  "defaultYear": 0,
  "createdAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|jobName|string|false|none||none|
|startTime|string|false|none||none|
|endTime|string|false|none||none|
|status|string|false|none||RUNNING, SUCCESS, FAILED|
|inputDeliverPath|string|false|none||none|
|inputPickupPath|string|false|none||none|
|outputPath|string|false|none||none|
|processedRecords|integer(int64)|false|none||none|
|errorMessage|string|false|none||none|
|executionTimeSeconds|integer|false|none||none|
|timeFormatUsed|string|false|none||none|
|defaultYear|integer|false|none||none|
|createdAt|string|false|none||none|

<h2 id="tocS_MapObject">MapObject</h2>

<a id="schemamapobject"></a>
<a id="schema_MapObject"></a>
<a id="tocSmapobject"></a>
<a id="tocsmapobject"></a>

```json
{
  "key": {}
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|key|[key](#schemakey)|false|none||none|

<h2 id="tocS_SimpleResponseMapObject">SimpleResponseMapObject</h2>

<a id="schemasimpleresponsemapobject"></a>
<a id="schema_SimpleResponseMapObject"></a>
<a id="tocSsimpleresponsemapobject"></a>
<a id="tocssimpleresponsemapobject"></a>

```json
{
  "success": true,
  "message": "string",
  "data": {
    "key": {}
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[MapObject](#schemamapobject)|false|none||none|

<h2 id="tocS_key1">key1</h2>

<a id="schemakey1"></a>
<a id="schema_key1"></a>
<a id="tocSkey1"></a>
<a id="tocskey1"></a>

```json
{}

```

### 属性

*None*

<h2 id="tocS_SimpleResponseListMapObject">SimpleResponseListMapObject</h2>

<a id="schemasimpleresponselistmapobject"></a>
<a id="schema_SimpleResponseListMapObject"></a>
<a id="tocSsimpleresponselistmapobject"></a>
<a id="tocssimpleresponselistmapobject"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "key": {}
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[MapObject](#schemamapobject)]|false|none||none|

<h2 id="tocS_TimeEfficiencyDTO">TimeEfficiencyDTO</h2>

<a id="schematimeefficiencydto"></a>
<a id="schema_TimeEfficiencyDTO"></a>
<a id="tocStimeefficiencydto"></a>
<a id="tocstimeefficiencydto"></a>

```json
{
  "city": "string",
  "date": "string",
  "totalDeliveries": 0,
  "avgDeliveryTime": 0,
  "fastDeliveries": 0,
  "slowDeliveries": 0,
  "fastDeliveryRate": 0,
  "slowDeliveryRate": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|date|string|false|none||对应数据库的date字段|
|totalDeliveries|integer(int64)|false|none||none|
|avgDeliveryTime|number|false|none||none|
|fastDeliveries|integer(int64)|false|none||none|
|slowDeliveries|integer(int64)|false|none||none|
|fastDeliveryRate|number|false|none||none|
|slowDeliveryRate|number|false|none||none|

<h2 id="tocS_SimpleResponseListTimeEfficiencyDTO">SimpleResponseListTimeEfficiencyDTO</h2>

<a id="schemasimpleresponselisttimeefficiencydto"></a>
<a id="schema_SimpleResponseListTimeEfficiencyDTO"></a>
<a id="tocSsimpleresponselisttimeefficiencydto"></a>
<a id="tocssimpleresponselisttimeefficiencydto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "date": "string",
      "totalDeliveries": 0,
      "avgDeliveryTime": 0,
      "fastDeliveries": 0,
      "slowDeliveries": 0,
      "fastDeliveryRate": 0,
      "slowDeliveryRate": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[TimeEfficiencyDTO](#schematimeefficiencydto)]|false|none||none|

<h2 id="tocS_TimeEfficiencyMetrics">TimeEfficiencyMetrics</h2>

<a id="schematimeefficiencymetrics"></a>
<a id="schema_TimeEfficiencyMetrics"></a>
<a id="tocStimeefficiencymetrics"></a>
<a id="tocstimeefficiencymetrics"></a>

```json
{
  "city": "string",
  "date": "string",
  "hour": 0,
  "totalDeliveries": 0,
  "avgDeliveryTime": 0,
  "medianDeliveryTime": 0,
  "p95DeliveryTime": 0,
  "fastDeliveries": 0,
  "normalDeliveries": 0,
  "slowDeliveries": 0,
  "fastDeliveryRate": 0,
  "slowDeliveryRate": 0,
  "totalPickups": 0,
  "avgPickupTime": 0,
  "medianPickupTime": 0,
  "p95PickupTime": 0,
  "fastPickups": 0,
  "normalPickups": 0,
  "slowPickups": 0,
  "onTimePickups": 0,
  "fastPickupRate": 0,
  "slowPickupRate": 0,
  "onTimePickupRate": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|date|string|false|none||none|
|hour|integer|false|none||none|
|totalDeliveries|integer(int64)|false|none||none|
|avgDeliveryTime|number|false|none||none|
|medianDeliveryTime|number|false|none||none|
|p95DeliveryTime|number|false|none||none|
|fastDeliveries|integer(int64)|false|none||none|
|normalDeliveries|integer(int64)|false|none||none|
|slowDeliveries|integer(int64)|false|none||none|
|fastDeliveryRate|number|false|none||none|
|slowDeliveryRate|number|false|none||none|
|totalPickups|integer(int64)|false|none||none|
|avgPickupTime|number|false|none||none|
|medianPickupTime|number|false|none||none|
|p95PickupTime|number|false|none||none|
|fastPickups|integer(int64)|false|none||none|
|normalPickups|integer(int64)|false|none||none|
|slowPickups|integer(int64)|false|none||none|
|onTimePickups|integer(int64)|false|none||none|
|fastPickupRate|number|false|none||none|
|slowPickupRate|number|false|none||none|
|onTimePickupRate|number|false|none||none|

<h2 id="tocS_SpatialAnalysisDTO">SpatialAnalysisDTO</h2>

<a id="schemaspatialanalysisdto"></a>
<a id="schema_SpatialAnalysisDTO"></a>
<a id="tocSspatialanalysisdto"></a>
<a id="tocsspatialanalysisdto"></a>

```json
{
  "city": "string",
  "date": "string",
  "lngGrid": 0,
  "latGrid": 0,
  "deliveryCount": 0,
  "uniqueCouriers": 0,
  "avgDeliveryTime": 0,
  "avgDeliveryDistance": 0,
  "deliveryDensity": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|date|string|false|none||对应数据库的date字段|
|lngGrid|number|false|none||none|
|latGrid|number|false|none||none|
|deliveryCount|integer(int64)|false|none||none|
|uniqueCouriers|integer(int64)|false|none||none|
|avgDeliveryTime|number|false|none||none|
|avgDeliveryDistance|number|false|none||none|
|deliveryDensity|number|false|none||none|

<h2 id="tocS_SimpleResponseListSpatialAnalysisDTO">SimpleResponseListSpatialAnalysisDTO</h2>

<a id="schemasimpleresponselistspatialanalysisdto"></a>
<a id="schema_SimpleResponseListSpatialAnalysisDTO"></a>
<a id="tocSsimpleresponselistspatialanalysisdto"></a>
<a id="tocssimpleresponselistspatialanalysisdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "date": "string",
      "lngGrid": 0,
      "latGrid": 0,
      "deliveryCount": 0,
      "uniqueCouriers": 0,
      "avgDeliveryTime": 0,
      "avgDeliveryDistance": 0,
      "deliveryDensity": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[SpatialAnalysisDTO](#schemaspatialanalysisdto)]|false|none||none|

<h2 id="tocS_PredictiveAnalysisDTO">PredictiveAnalysisDTO</h2>

<a id="schemapredictiveanalysisdto"></a>
<a id="schema_PredictiveAnalysisDTO"></a>
<a id="tocSpredictiveanalysisdto"></a>
<a id="tocspredictiveanalysisdto"></a>

```json
{
  "city": "string",
  "regionId": "string",
  "dsDate": "string",
  "hour": 0,
  "orderVolume": 0,
  "courierCount": 0,
  "avgDuration": 0,
  "totalDistance": 0,
  "volumeTrend": 0,
  "efficiencyScore": 0,
  "dataType": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|regionId|string|false|none||none|
|dsDate|string|false|none||对应数据库的ds_date字段|
|hour|integer|false|none||none|
|orderVolume|integer(int64)|false|none||none|
|courierCount|integer(int64)|false|none||none|
|avgDuration|number|false|none||none|
|totalDistance|number|false|none||none|
|volumeTrend|integer(int64)|false|none||none|
|efficiencyScore|number|false|none||none|
|dataType|string|false|none||none|

<h2 id="tocS_SimpleResponseTimeEfficiencyDTO">SimpleResponseTimeEfficiencyDTO</h2>

<a id="schemasimpleresponsetimeefficiencydto"></a>
<a id="schema_SimpleResponseTimeEfficiencyDTO"></a>
<a id="tocSsimpleresponsetimeefficiencydto"></a>
<a id="tocssimpleresponsetimeefficiencydto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": {
    "city": "string",
    "date": "string",
    "totalDeliveries": 0,
    "avgDeliveryTime": 0,
    "fastDeliveries": 0,
    "slowDeliveries": 0,
    "fastDeliveryRate": 0,
    "slowDeliveryRate": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[TimeEfficiencyDTO](#schematimeefficiencydto)|false|none||com.logistics.service.dto.TimeEfficiencyDTO|

<h2 id="tocS_SimpleResponseListPredictiveAnalysisDTO">SimpleResponseListPredictiveAnalysisDTO</h2>

<a id="schemasimpleresponselistpredictiveanalysisdto"></a>
<a id="schema_SimpleResponseListPredictiveAnalysisDTO"></a>
<a id="tocSsimpleresponselistpredictiveanalysisdto"></a>
<a id="tocssimpleresponselistpredictiveanalysisdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "regionId": "string",
      "dsDate": "string",
      "hour": 0,
      "orderVolume": 0,
      "courierCount": 0,
      "avgDuration": 0,
      "totalDistance": 0,
      "volumeTrend": 0,
      "efficiencyScore": 0,
      "dataType": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[PredictiveAnalysisDTO](#schemapredictiveanalysisdto)]|false|none||none|

<h2 id="tocS_SpatialAnalysisMetrics">SpatialAnalysisMetrics</h2>

<a id="schemaspatialanalysismetrics"></a>
<a id="schema_SpatialAnalysisMetrics"></a>
<a id="tocSspatialanalysismetrics"></a>
<a id="tocsspatialanalysismetrics"></a>

```json
{
  "city": "string",
  "date": "string",
  "lngGrid": 0,
  "latGrid": 0,
  "deliveryCount": 0,
  "uniqueCouriers": 0,
  "avgDeliveryTime": 0,
  "avgDeliveryDistance": 0,
  "deliveryDensity": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|date|string|false|none||none|
|lngGrid|number|false|none||none|
|latGrid|number|false|none||none|
|deliveryCount|integer(int64)|false|none||none|
|uniqueCouriers|integer(int64)|false|none||none|
|avgDeliveryTime|number|false|none||none|
|avgDeliveryDistance|number|false|none||none|
|deliveryDensity|number|false|none||none|

<h2 id="tocS_ComprehensiveReportDTO">ComprehensiveReportDTO</h2>

<a id="schemacomprehensivereportdto"></a>
<a id="schema_ComprehensiveReportDTO"></a>
<a id="tocScomprehensivereportdto"></a>
<a id="tocscomprehensivereportdto"></a>

```json
{
  "city": "string",
  "regionId": 0,
  "date": "string",
  "totalDeliveries": 0,
  "activeCouriers": 0,
  "servedAois": 0,
  "avgDeliveryTime": 0,
  "totalDistance": 0,
  "fastDeliveries": 0,
  "avgOrdersPerCourier": 0,
  "avgDistancePerOrder": 0,
  "fastDeliveryRate": 0,
  "reportType": "string",
  "generatedAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|regionId|integer|false|none||none|
|date|string|false|none||none|
|totalDeliveries|integer(int64)|false|none||none|
|activeCouriers|integer(int64)|false|none||none|
|servedAois|integer(int64)|false|none||none|
|avgDeliveryTime|number|false|none||none|
|totalDistance|number|false|none||none|
|fastDeliveries|integer(int64)|false|none||none|
|avgOrdersPerCourier|number|false|none||none|
|avgDistancePerOrder|number|false|none||none|
|fastDeliveryRate|number|false|none||none|
|reportType|string|false|none||none|
|generatedAt|string|false|none||none|

<h2 id="tocS_SimpleResponseListComprehensiveReportDTO">SimpleResponseListComprehensiveReportDTO</h2>

<a id="schemasimpleresponselistcomprehensivereportdto"></a>
<a id="schema_SimpleResponseListComprehensiveReportDTO"></a>
<a id="tocSsimpleresponselistcomprehensivereportdto"></a>
<a id="tocssimpleresponselistcomprehensivereportdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "regionId": 0,
      "date": "string",
      "totalDeliveries": 0,
      "activeCouriers": 0,
      "servedAois": 0,
      "avgDeliveryTime": 0,
      "totalDistance": 0,
      "fastDeliveries": 0,
      "avgOrdersPerCourier": 0,
      "avgDistancePerOrder": 0,
      "fastDeliveryRate": 0,
      "reportType": "string",
      "generatedAt": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[ComprehensiveReportDTO](#schemacomprehensivereportdto)]|false|none||none|

<h2 id="tocS_OperationalEfficiencyDTO">OperationalEfficiencyDTO</h2>

<a id="schemaoperationalefficiencydto"></a>
<a id="schema_OperationalEfficiencyDTO"></a>
<a id="tocSoperationalefficiencydto"></a>
<a id="tocsoperationalefficiencydto"></a>

```json
{
  "city": "string",
  "regionId": 0,
  "courierId": 0,
  "date": "string",
  "totalOrders": 0,
  "uniqueAoiServed": 0,
  "totalDistance": 0,
  "totalWorkingHours": 0,
  "avgDeliveryTime": 0,
  "ordersPerHour": 0,
  "distancePerOrder": 0,
  "efficiencyScore": 0,
  "efficiencyLevel": "string",
  "workloadLevel": "string",
  "aoiCoverageRate": 0,
  "distanceEfficiency": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|regionId|integer|false|none||none|
|courierId|integer|false|none||none|
|date|string|false|none||改为date，与数据库字段一致|
|totalOrders|integer(int64)|false|none||none|
|uniqueAoiServed|integer(int64)|false|none||none|
|totalDistance|number|false|none||none|
|totalWorkingHours|number|false|none||none|
|avgDeliveryTime|number|false|none||none|
|ordersPerHour|number|false|none||none|
|distancePerOrder|number|false|none||none|
|efficiencyScore|number|false|none||none|
|efficiencyLevel|string|false|none||新增字段：便于前端展示和分析|
|workloadLevel|string|false|none||工作强度等级|
|aoiCoverageRate|number|false|none||AOI覆盖率|
|distanceEfficiency|number|false|none||距离效率|

<h2 id="tocS_PredictiveAnalysisData">PredictiveAnalysisData</h2>

<a id="schemapredictiveanalysisdata"></a>
<a id="schema_PredictiveAnalysisData"></a>
<a id="tocSpredictiveanalysisdata"></a>
<a id="tocspredictiveanalysisdata"></a>

```json
{
  "city": "string",
  "dsDate": "string",
  "hour": 0,
  "orderVolume": 0,
  "courierCount": 0,
  "avgDuration": 0,
  "totalDistance": 0,
  "volumeTrend": 0,
  "efficiencyScore": 0,
  "dataType": "string",
  "regionId": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|dsDate|string|false|none||none|
|hour|integer|false|none||none|
|orderVolume|integer(int64)|false|none||none|
|courierCount|integer(int64)|false|none||none|
|avgDuration|number|false|none||none|
|totalDistance|number|false|none||none|
|volumeTrend|integer(int64)|false|none||none|
|efficiencyScore|number|false|none||none|
|dataType|string|false|none||none|
|regionId|string|false|none||none|

<h2 id="tocS_SimpleResponseListOperationalEfficiencyDTO">SimpleResponseListOperationalEfficiencyDTO</h2>

<a id="schemasimpleresponselistoperationalefficiencydto"></a>
<a id="schema_SimpleResponseListOperationalEfficiencyDTO"></a>
<a id="tocSsimpleresponselistoperationalefficiencydto"></a>
<a id="tocssimpleresponselistoperationalefficiencydto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    {
      "city": "string",
      "regionId": 0,
      "courierId": 0,
      "date": "string",
      "totalOrders": 0,
      "uniqueAoiServed": 0,
      "totalDistance": 0,
      "totalWorkingHours": 0,
      "avgDeliveryTime": 0,
      "ordersPerHour": 0,
      "distancePerOrder": 0,
      "efficiencyScore": 0,
      "efficiencyLevel": "string",
      "workloadLevel": "string",
      "aoiCoverageRate": 0,
      "distanceEfficiency": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[[OperationalEfficiencyDTO](#schemaoperationalefficiencydto)]|false|none||none|

<h2 id="tocS_SimpleResponseComprehensiveReportDTO">SimpleResponseComprehensiveReportDTO</h2>

<a id="schemasimpleresponsecomprehensivereportdto"></a>
<a id="schema_SimpleResponseComprehensiveReportDTO"></a>
<a id="tocSsimpleresponsecomprehensivereportdto"></a>
<a id="tocssimpleresponsecomprehensivereportdto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": {
    "city": "string",
    "regionId": 0,
    "date": "string",
    "totalDeliveries": 0,
    "activeCouriers": 0,
    "servedAois": 0,
    "avgDeliveryTime": 0,
    "totalDistance": 0,
    "fastDeliveries": 0,
    "avgOrdersPerCourier": 0,
    "avgDistancePerOrder": 0,
    "fastDeliveryRate": 0,
    "reportType": "string",
    "generatedAt": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[ComprehensiveReportDTO](#schemacomprehensivereportdto)|false|none||com.logistics.service.dto.ComprehensiveReportDTO|

<h2 id="tocS_SimpleResponseListString">SimpleResponseListString</h2>

<a id="schemasimpleresponseliststring"></a>
<a id="schema_SimpleResponseListString"></a>
<a id="tocSsimpleresponseliststring"></a>
<a id="tocssimpleresponseliststring"></a>

```json
{
  "success": true,
  "message": "string",
  "data": [
    "string"
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[string]|false|none||none|

<h2 id="tocS_SimpleResponseLong">SimpleResponseLong</h2>

<a id="schemasimpleresponselong"></a>
<a id="schema_SimpleResponseLong"></a>
<a id="tocSsimpleresponselong"></a>
<a id="tocssimpleresponselong"></a>

```json
{
  "success": true,
  "message": "string",
  "data": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|integer(int64)|false|none||none|

<h2 id="tocS_ResponseEntityMapObject">ResponseEntityMapObject</h2>

<a id="schemaresponseentitymapobject"></a>
<a id="schema_ResponseEntityMapObject"></a>
<a id="tocSresponseentitymapobject"></a>
<a id="tocsresponseentitymapobject"></a>

```json
{
  "key": {}
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|key|object|false|none||none|

<h2 id="tocS_CostAnalysisMetrics">CostAnalysisMetrics</h2>

<a id="schemacostanalysismetrics"></a>
<a id="schema_CostAnalysisMetrics"></a>
<a id="tocScostanalysismetrics"></a>
<a id="tocscostanalysismetrics"></a>

```json
{
  "city": "string",
  "regionId": 0,
  "date": "string",
  "totalCost": 0,
  "totalFuelCost": 0,
  "totalTimeCost": 0,
  "totalOrders": 0,
  "totalDistance": 0,
  "costPerOrder": 0,
  "costPerKm": 0,
  "fuelCostRatio": 0,
  "analysisType": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|regionId|integer|false|none||none|
|date|string|false|none||none|
|totalCost|number|false|none||none|
|totalFuelCost|number|false|none||none|
|totalTimeCost|number|false|none||none|
|totalOrders|integer(int64)|false|none||none|
|totalDistance|number|false|none||none|
|costPerOrder|number|false|none||none|
|costPerKm|number|false|none||none|
|fuelCostRatio|number|false|none||none|
|analysisType|string|false|none||none|

<h2 id="tocS_OperationalEfficiencyMetrics">OperationalEfficiencyMetrics</h2>

<a id="schemaoperationalefficiencymetrics"></a>
<a id="schema_OperationalEfficiencyMetrics"></a>
<a id="tocSoperationalefficiencymetrics"></a>
<a id="tocsoperationalefficiencymetrics"></a>

```json
{
  "city": "string",
  "regionId": 0,
  "courierId": 0,
  "date": "string",
  "totalOrders": 0,
  "uniqueAoiServed": 0,
  "totalDistance": 0,
  "totalWorkingHours": 0,
  "avgDeliveryTime": 0,
  "ordersPerHour": 0,
  "distancePerOrder": 0,
  "efficiencyScore": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|city|string|false|none||none|
|regionId|integer|false|none||none|
|courierId|integer|false|none||none|
|date|string|false|none||none|
|totalOrders|integer(int64)|false|none||none|
|uniqueAoiServed|integer(int64)|false|none||none|
|totalDistance|number|false|none||none|
|totalWorkingHours|number|false|none||none|
|avgDeliveryTime|number|false|none||none|
|ordersPerHour|number|false|none||none|
|distancePerOrder|number|false|none||none|
|efficiencyScore|number|false|none||none|

<h2 id="tocS_SimpleResponseOperationalEfficiencyDTO">SimpleResponseOperationalEfficiencyDTO</h2>

<a id="schemasimpleresponseoperationalefficiencydto"></a>
<a id="schema_SimpleResponseOperationalEfficiencyDTO"></a>
<a id="tocSsimpleresponseoperationalefficiencydto"></a>
<a id="tocssimpleresponseoperationalefficiencydto"></a>

```json
{
  "success": true,
  "message": "string",
  "data": {
    "city": "string",
    "regionId": 0,
    "courierId": 0,
    "date": "string",
    "totalOrders": 0,
    "uniqueAoiServed": 0,
    "totalDistance": 0,
    "totalWorkingHours": 0,
    "avgDeliveryTime": 0,
    "ordersPerHour": 0,
    "distancePerOrder": 0,
    "efficiencyScore": 0,
    "efficiencyLevel": "string",
    "workloadLevel": "string",
    "aoiCoverageRate": 0,
    "distanceEfficiency": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|success|boolean|false|none||none|
|message|string|false|none||none|
|data|[OperationalEfficiencyDTO](#schemaoperationalefficiencydto)|false|none||com.logistics.service.dto.OperationalEfficiencyDTO|

