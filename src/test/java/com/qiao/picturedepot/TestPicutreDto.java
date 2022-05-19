package com.qiao.picturedepot;

import com.qiao.picturedepot.dao.PictureIdentityMapper;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@SpringBootTest
public class TestPicutreDto {
    @Autowired
    PictureIdentityMapper pictureIdentityMapper;

    @Test
    public void getPictureIdentity(){
        PictureIdentity pictureIdentity = pictureIdentityMapper.getById(2L);
        byte[] md5 = pictureIdentity.getMd5();

        for (byte b : md5) {
            int h = (b & 0xf0) >> 4;
            int l = (b & 0x0f);
            char x = (char) (h < 10 ? h + '0' : h - 10 + 'a');
            char y = (char) (l < 10 ? l + '0' : l - 10 + 'a');
            System.out.print(x);
            System.out.print(y);
        }
        System.out.println("");
        //5d41402abc4b2a76b9719d911017c592
    }

    @Test
    public void addPictureIdentity(){
        PictureIdentity pictureIdentity = new PictureIdentity();
        pictureIdentity.setFormat("xxx");
        pictureIdentity.setUri("hello.xxx");
        pictureIdentity.setMd5(FileUtil.md5Digest("hello".getBytes(StandardCharsets.UTF_8)));

        pictureIdentityMapper.add(pictureIdentity);

        System.out.println(DigestUtils.md5DigestAsHex("hello".getBytes(StandardCharsets.UTF_8)));
    }
}
