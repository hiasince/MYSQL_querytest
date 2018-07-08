# MYSQL_querytest
Performance comparison with Static query and Dynamic query and Procedure call in MYSQL(Language : java)

## Concept

* Test each query and performance comparison with Static query and Dynamic query and Procedure call in MYSQL

    * Static Query
    * Dynamic Query
    * Dynamic Query with parameter binding
    * Procedure call

## Test Method

* Test Driver : MySQL Connector/J 5.1
* Test Table Schema

```
CREATE TABLE `tst_tbl` (
  `seq` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `sno` bigint(20) NOT NULL,
  `item_id` varchar(50) NOT NULL,
  `regdate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_sno` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

```

* Test Query Set

```
insert tst_tbl (sno, item_id) values ([1~10000까지 random 한 값], [uuid()값] );
select * from tst_tbl where seq = [LAST_INSERT_ID()값];
update  tst_tbl set item_id = [uuid()값] where seq = [LAST_INSERT_ID()값];

```

* Test Procedure

```
DELIMITER $$
CREATE PROCEDURE `usp_tst_tbl`(IN _sno bigint, IN _uuid1 varchar(50), IN _uuid2 varchar(50))
BEGIN
    insert tst_tbl (sno, item_id) values (_sno, _uuid1 );
    select * from tst_tbl where seq = LAST_INSERT_ID();
    update  tst_tbl set item_id = _uuid2 where seq = LAST_INSERT_ID();
END
$$
DELIMITER ;

```

* Test Process
    * Query test
        * 1 thread
        * 100,000 record "insert / select / update" (Total 30,000 Query)
        
    * Procedure test
        * 1 thread
        * 100,000 call
        
    * Turncate table after every test `truncate table tst_tbl;`
    * Test 3 times each case.
    
* Advance preparation
    * Pre-check inflow query contents : Before test, check inflow query with general log in MYSQL when application execute query.
    * Pre-check DB Server Monitor : Before test, check DB Resource ( Connection / Network IO / CPU / Disk IO )

| Case | Feature |
| --- | --- |
| <span style="color:#555555">Static query</span> | Standard Query |
| <span style="color:#555555">Dynamic query</span> | useServerPrepStmts=false (default) |
| <span style="color:#555555">Dynamic query (1 prepare 1 execute)</span> | <span style="color:#1d2129">useServerPrepStmts=true</span> |
| <span style="color:#555555">Dynamic query (many time execute)</span> | <span style="color:#1d2129">useServerPrepStmts=true</span> <span style="color:#555555">1 time prepare & 10000 time execute</span> |
| Procedure <span style="color:#555555">(no prepare)</span> |  |
| Static (3 query in 1time) | call 3 query set in 1 time |
| Procedure (call each query) | divide 1 procedure to 3 procedure |

## Test Result - 1 thread 100,000 * 3 query

| No | Case | Execution time | DB Resource | Server Resource | Note |
| --- | --- | ---- | --------- | -------- | --- |
| 1 | <span style="color:#555555">Static query</span> | 231.6 | Standard | Standard |  |
| 2 | <span style="color:#555555">Dynamic query</span> | 239.3 |  |  | Increase execution time 2.5%  |
| 3 | <span style="color:#555555">Dynamic query (1 prepare 1 execute)</span> | 392.4 | Prepare phase use additional networks. | increase network usage |  |
| 4 | <span style="color:#555555">Dynamic query (many time execute)</span> | 233.3 | Reduced receive network usage 30 % | Reduce output network | Reduce network usage |
| 5 | Procedure <span style="color:#555555">(</span>3 query 1 procedure<span style="color:#555555">)</span> | 129.3 | Reduced receive network usage 2 % | Reduce output network | Reduced execution time to 55.8 %<br>High throughput |
| 6 | Static (3 query in 1 time) | 118.6 |  |  | Highest throughput |
| 7 | Procedure (1 query 1 procedure) | 242.6 |  | Increase receive network 30%<br>Increase output network 16%  | Increase execution time 7%<br>Increase network usage |

## Conclusion

1.  Static query VS Dynamic Query

| No | Case | Execution time |
| --- | --- | ---- |
| Case 1 | Static query | 231.6 |
| Case 4 | Dynamic query (many time execute) | 233.3 |
| Case 2 | Dynamic query | 239.3 |
| Case 3 | Dynamic query (1 prepare 1 execute) | 392.4 |

A comparison of static queries with dynamic queries in MYSQL, static query has best result in the cases.<br>
In case 4, Dynamic query got good results. But this is the result of only specific situation. Rather, They shows low performance in the worst case scenario. When you use connection pool, dynamic query would shows low performance.

2\. Procedure VS Query

| No | Case | Execution time |
| --- | --- | ---- |
| Case 1 | Static query | 231.6 |
| Case 7 | Procedure (1 query 1 procedure) | 242.6 |
| Case 6 | Static (3 query in 1 time) | 118.6 |
| Case 5 | Procedure <span style="color:#555555">(3 query 1 procedure)</span> | 129.3 |

At the first time, using Procedure perform far faster, so I doubt it is working more efficiently.
But If you dissolve one query in a one procedure, you can clear the doubt. sending three static queries in one step perform better.
We could see that it was the call count that affected performance, not the difference between query and processor. Rather, executing a process once more has led to the select statement being executed, which is inefficient. and if you use procedure, when ERROR occur, hard to find the problem query. because MYSQL only save procedure and dose not save inside queries.

Conclusion, <span style="color:#e11d21">**Using Static query**</span> is the best, <span style="color:#e11d21">**Less call count**</span> is the best.

## Reference

* Add SQL injection code

Static query can't prevent the sql injection. so you have to add prevent code.<br>
Check sql length, unsecure string(select, delete, update..), type.

| No | Case | Execution time | DB Resource | Server Resource | Note |
| --- | --- | ---- | --------- | -------- | --- |
| Case 0 | <span style="color:#555555">Static query ( SQL injection)</span> | 241.5 | Standard | Standard |  |


* <span style="color:  #1d2129;;">useServerPrepStmts property</span><br>

The default setting in JAVA useServerPrepStmts property is false. <br>
If you run false, Dynamic query works the same as the static query, and you see that it takes the same amount of time as the static query. <br>
If the code is already configured with Dynamic but you want to perform Static, you just set the useServerPrepStmts property false and insert the SQL injection protection code only. <br>
In case, migration Oracle to MYSQL, generally oracle code configured with Dynamaic code. You don't have to chage all the codes, just chage useServerPrepStmts property. <br>

* useLocalSessionState property<br>

In Case 5(Procedure call), `select @@session.tx_read_only` additional queries had been inflow.<br>
The addition of a setting called useLocalSessionState=true found to address this phenomenon, it reduces the execution time, and the query has disappeared.

