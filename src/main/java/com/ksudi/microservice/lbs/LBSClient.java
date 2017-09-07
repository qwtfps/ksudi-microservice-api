package com.ksudi.microservice.lbs;

import feign.Param;
import feign.RequestLine;

/**
 * Created by ICE on 2016/12/10.
 */
public interface LBSClient {

    /*查询区域详情*/
    @RequestLine("GET /lbs/area?cityid={id}")
    String area(@Param("id") String id);


    /*查询城市区域列表*/
    @RequestLine("GET /lbs/city/area?cityid={cityid}&pageNum={pageNum}")
    String arealist(@Param("cityid") String cityid, @Param("pageNum") Integer pageNum);


    /*重置缓存*/
    @RequestLine("GET /lbs/city/cache?cityid={cityid}&level={level}")
    void resetCache(@Param("cityid") String cityid, @Param("level") Integer level);

    /*地址解析*/
    @RequestLine("GET /lbs/address?cityid={cityid}&address={address}")
    String address(@Param("cityid") String cityid, @Param("address") String address);

}
