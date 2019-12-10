package com.heyuan.gmall.manage.servive.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import com.heyuan.gmall.bean.*;
import com.heyuan.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.heyuan.gmall.manage.mapper.PmsSkuImageMapper;
import com.heyuan.gmall.manage.mapper.PmsSkuInfoMapper;
import com.heyuan.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.heyuan.gmall.service.SkuService;
import com.heyuan.gmall.util.RedisUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SkuService skuService;

    @Autowired
    JestClient jestClient;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        try {
            put();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //添加sku时同时上传到es服务器
    public void put() throws IOException {

        // 查询mysql数据
        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();

        pmsSkuInfoList = skuService.getAllSku("61");

        // 转化为es的数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();

            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);

            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));

            pmsSearchSkuInfos.add(pmsSearchSkuInfo);

        }

        // 导入es
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            //参数分别对应es的数据-库名-表名-主键
            Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
            jestClient.execute(put);
        }

    }


    //  @Override
//    public PmsSkuInfo getSkuById(String skuId) {
//        //sku的商品对象
//        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
//        pmsSkuInfo.setId(skuId);
//        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
//
//        try {
//            //sku的图片集合
//            PmsSkuImage pmsSkuImage = new PmsSkuImage();
//            pmsSkuImage.setSkuId(skuId);
//            List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
//            skuInfo.setSkuImageList(pmsSkuImages);
//     } catch (Exception e) {
//     e.printStackTrace();
//     }
//     return skuInfo;
//     }

     /**
     * 从数据库调用
     *
     * @param skuId
     * @return
     */
    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        //sku的商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        try {
            //sku的图片集合
            PmsSkuImage pmsSkuImage = new PmsSkuImage();
            pmsSkuImage.setSkuId(skuId);
            List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
            skuInfo.setSkuImageList(pmsSkuImages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skuInfo;

    }


    /**
     * 商品详细图
     * 主要是item前端的东西，调用此处的服务，方便
     * 使用Redis缓存，解决高并发
     *
     */
    @Override
    public PmsSkuInfo getSkuById(String skuId, String ip) {
        System.out.println("ip:" + ip + "成功" + Thread.currentThread().getName()+"从缓存中获取商品详情");

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        //链接缓存
        Jedis jedis = redisUtil.getJedis();

        //查询缓存
        String skuKey = "sky:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);
        //可以吧json的字符串转换成java的对象类
        if (StringUtils.isNotBlank(skuJson)) {// if (skuJson!=null&&!skuJson.equals(""))
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        } else {
            //如果缓存没有，查询mysql
            System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"发现缓存中没有，申请缓存的分布式锁："+"sku:" + skuId + ":lock");

            //先设置分布式锁
            String token = UUID.randomUUID().toString();
            String OK = jedis.set("sky:" + skuId + ":lock", token, "nx", "px", 1000 * 10);//拿到锁的线程有10秒过期时间
            if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                //设置成功，有权在10秒的过期时间内访问数据库
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"有权在10秒的过期时间内访问数据库："+"sku:" + skuId + ":lock");

                pmsSkuInfo = getSkuByIdFromDb(skuId);
                if (pmsSkuInfo != null) {
                    //mysql查询结果存入redis
                    jedis.set("sku" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));

                    //在访问mysql后，将mysql的分布式锁释放
                    System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"使用完毕，将锁归还："+"sku:" + skuId + ":lock");
                    String lockToken = jedis.get("sku" + skuId + ":lock");
                    if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)) {

                        jedis.del("sku" + skuId + ":lock");//用token确定删除是自己的sku的锁
                    }

                } else {
                    //数据库中不存在sku
                    //为防止缓存穿透，将null或者空字符串设置给redis
                    jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));

                }
            } else {
                //设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"没有拿到锁，开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId, ip);
            }
        }
        jedis.close();

        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }

    //查询所有sku属性
    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos=pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo:pmsSkuInfos){
            String skuId=pmsSkuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue=new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select=pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }


}
