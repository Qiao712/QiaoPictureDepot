package com.qiao.picturedepot;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.PictureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.math.BigInteger;

@SpringBootTest
public class RedisTest {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private AlbumService albumService;
    private Jedis jedis = new Jedis("8.141.151.176", 6379);

    @Test
    void TestJedis(){
        jedis.auth("lty0712");
        System.out.println(jedis.ping());
    }

    //使用redis缓存优化，对picture group的owner的反复读取
    @Test
    void TestRepeatedlyGetOwnerOfPictureGroupWithRedis(){
        BigInteger pictureGroupId = BigInteger.valueOf(59);

        for(int i = 0; i < 100; i++){
            String owner = jedis.get(pictureGroupId.toString());
            if(owner == null){
                //从sql数据库中查询picture group拥有者
                PictureGroup pictureGroup = pictureService.getPictureGroupById(pictureGroupId);
                Album album = null;
                if(pictureGroup != null){
                    BigInteger albumId = pictureGroup.getAlbum();
                    if(albumId != null){
                        album = albumService.getAlbumById(albumId);
                    }
                }
                owner = album.getOwner().toString();

                //缓存
                jedis.set(pictureGroupId.toString(), owner);
            }

            System.out.println(owner);
        }
    }

    @Test
    void TestRepeatedlyGetOwnerOfPictureGroup(){
        for(int i = 0; i<100; i++){
            BigInteger pictureGroupId = BigInteger.valueOf(59);
            PictureGroup pictureGroup = pictureService.getPictureGroupById(pictureGroupId);
            Album album = null;
            if(pictureGroup != null){
                BigInteger albumId = pictureGroup.getAlbum();
                if(albumId != null){
                    album = albumService.getAlbumById(albumId);
                }
            }
            album.getOwner();
        }
    }
}
