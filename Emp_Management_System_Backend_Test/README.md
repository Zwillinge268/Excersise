# 模擬簡單員工管理系統

### 功能簡介
* 透過前端請求對資料庫中員工(emp)表及部門(dept)表進行CRUD操作
* 登入發放JWT令牌，使用Filter過濾未授權(未包含有效令牌)的請求
* AOP設計方案，以切面類在所需連接點執行時記錄日志

### 系統概念圖
![Untitled]([https://hackmd.io/_uploads/BJjL5viP0.png](https://raw.githubusercontent.com/Zwillinge268/Project/master/Emp_Management_System_Backend_Test/map.png))

### Example table

emp
```sql
CREATE TABLE emp (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(40) NOT NULL,
  `phone` VARCHAR(40) NOT NULL,
  `dept_id` TINYINT UNSIGNED NOT NULL,
  `create_time` DATETIME NOT NULL,
  `last_update_time` DATETIME NOT NULL);
```
dept
```sql
CREATE TABLE dept (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(40) NOT NULL UNIQUE,
  `create_time` DATETIME NOT NULL,
  `last_update_time` DATETIME NOT NULL);
```
user
```sql
CREATE TABLE user (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL UNIQUE,
  `password` VARCHAR(30) NOT NULL);
```
log
```sql
CREATE TABLE log (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `operate_user` INT UNSIGNED NOT NULL,
  `operate_time` DATETIME NOT NULL,
  `method_name` VARCHAR(100) NOT NULL,
  `method_params` VARCHAR(500) NOT NULL,
  `return_value` VARCHAR(1000) NOT NULL);
```
<br/>

# API 文件
### 統一返回結果：Result (Json String)

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| code     | Integer     | 0為失敗，1為成功     |
| message     | String     | 返回相應信息     |
| data     | Array     | 所請求的數據     |

範例：
```json
{
    "code": 1,
    "message": "success",
    "data": null
}
```
### Filter過濾器標注
所有標注`@Filter`的方法都需要在Header携帶有效token才允許訪問

Request Header 範例

| token | eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImV4cCI6MTcyMDUxNjY3Mn0.55zewOQwcr7NK9KysvlTv7P9xCXytQ6KNxW2LcS6vFs |
| -------- | -------- |

失敗返回範例
```json
{
    "code": 0,
    "message": "Not login",
    "data": null
}
```

### AOP日志管理標注
所有標注`@Log`的方法都會在執行時記錄執行情況(不論成功與否)

記錄内容

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer     |    |
| operateUser  | Integer     |   User ID   |
| operateTime  | LocalDateTime |      |
| methodName   | String | 類名+方法名 |
| methodParams | String     |  前端傳入之參數  |
| returnValue  | String     |   伺服器所返回之字串   |

<br/>

## 員工(emp)相關
### 新增員工
説明：以提供的資料創建新員工

    @Filter 
    @Log
    
    請求地址： /emps
    請求方法： POST
    
請求參數：Json in Request body

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| name     | String   |          |
| phone     | String   |          |
| deptId     | Integer   | From table 'dept'   |

請求範例
```json
{
    "name": "Herry Lai",
    "phone": "61234567",
    "deptId": 2
}
```

返回結果：Result

返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": null
}
```

### 刪除員工
説明：根據員工ID刪除員工，可批量刪除

    @Filter 
    @Log
    
    請求地址： /emps/{ids}
    請求方法： DELETE
    
請求參數：Variable in Path

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| ids     | List\<Integer>   |  split id by ' , '  |

請求範例
```
http://localhost/emps/5,6
```

返回結果：Result

返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": null
}
```

### 修改員工
説明：更改指定員工ID的資料

    @Filter 
    @Log
    
    請求地址： /emps
    請求方法： PUT
    
請求參數：Json in Request body

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer   |  Target emp id  |
| name     | String   |    |
| phone     | String   |    |
| deptId     | Integer   |  From table 'dept'  |

請求範例
```json
{
    "id": 10,
    "name": "Gerry Li",
    "phone": "91234567",
    "deptId": 3
}
```

返回結果：Result

返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": null
}
```

### 查詢員工 - by ID
説明：根據員工ID回顯員工資料，用於修改員工

    @Filter
    
    請求地址： /emps/id
    請求方法： GET
    
請求參數：Variable in Path

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer   |   |

請求範例
```
http://localhost/emps/1
```

返回結果：Result

data説明
| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer     |      |
| name   | String     |   |
| phone  | String     |   |
| deptId | Integer     |    |
| createTime | LocalDateTime |   |
| lastUpdateTime | LocalDateTime |  |


返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": {
        "id": 1,
        "name": "John Ma",
        "phone": "90901010",
        "deptId": 1,
        "createTime": "2024-07-03T11:10:07",
        "lastUpdateTime": "2024-07-03T11:10:07"
    }
}
```

### 查詢員工 - by info
説明：根據員工資料進行分頁查詢
    
    @Filter
    
    請求地址： /emps?page={page}&pagesize={pagesize}&name={name}&phone={phone}&deptId={deptId}
    請求方法： GET
    
請求參數：Variable in Path

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| page     | Integer   | 目前頁碼，default: 1  |
| pagesize | Integer | 每頁顯示資料數，default: 10|
| name     | String   | 可爲空，支持模糊查詢  |
| phone     | String   | 可爲空，支持模糊查詢  |
| deptId     | Integer   | 可爲空 |

請求範例
```
http://localhost/emps?page=1&pagesize=2&deptId=2
```

返回結果：Result

data説明

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| total     | Integer     | 員工資料總數     |
| rows     | List     | 員工資料内容     |

rows説明

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer     |      |
| name   | String     |   |
| phone  | String     |   |
| deptId | Integer     |    |
| createTime | LocalDateTime |   |
| lastUpdateTime | LocalDateTime |  |


返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": {
        "total": 10,
        "rows": [
            {
                "id": 1,
                "name": "John Ma",
                "phone": "90001234",
                "deptId": 2,
                "createTime": "2024-07-03T11:10:07",
                "lastUpdateTime": "2024-07-03T11:10:07"
            },
            {
                "id": 2,
                "name": "Joe Wong",
                "phone": "69992221",
                "deptId": 2,
                "createTime": "2024-07-04T12:00:10",
                "lastUpdateTime": "2024-07-04T12:00:10"
            }
        ]
        
    }
}
```
<br/>

## 部門(dept)相關
### 新增部門
説明：以提供的名稱建立新部門

    @Filter 
    @Log
    
    請求地址： /depts
    請求方法： POST
    
請求參數：Json in Request body

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| name     | String   |          |

請求範例
```json
{
    "name": "人事部"
}
```

返回結果：Result

返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": null
}
```
### 刪除部門
説明：根據部門ID刪除部門

    @Filter 
    @Log
    
    請求地址： /depts
    請求方法： DELETE
    
請求參數：Variable in Path

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer   |          |

請求範例
```
http://localhost/{id}
```

返回結果：Result

返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": null
}
```

### 查詢部門
説明：查詢所有部門部門

    @Filter
    
    請求地址： /depts
    請求方法： GET
    
請求參數：null

返回結果：Result

data説明

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| id     | Integer    |      |
| name   | String     |      |
| createTime | LocalDateTime |      |
| lastUpdateTime | LocalDateTime |      |


返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "人事資源部",
            "createTime": "2024-07-03T09:30:35",
            "lastUpdateTime": "2024-07-03T09:30:35"
        },
        {
           "id": 2,
            "name": "會計財務部",
            "createTime": "2024-07-03T09:30:39",
            "lastUpdateTime": "2024-07-03T09:30:35" 
        },
        {
            "id": 3,
            "name": "行銷宣傳部",
            "createTime": "2024-07-03T09:30:43",
            "lastUpdateTime": "2024-07-03T09:30:35"
        }
    ]
}
```

<br/>

## 登入相關
### 登入系統

説明：以使用者名稱及密碼獲取通行密鑰

    請求地址： /login
    請求方法： GET
    
請求參數：Json in Request body
| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| username   | String     |      |
| password   | String     |      |

返回結果：Result

| 名稱 | 類型 | 備注 |
| -------- | -------- | -------- |
| data     | String    |   JWT Token   |

返回範例
```json
{
    "code": 1,
    "message": "success",
    "data": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImV4cCI6MTcyMDUxNjY3Mn0.55zewOQwcr7NK9KysvlTv7P9xCXytQ6KNxW2LcS6vFs"
}
```
