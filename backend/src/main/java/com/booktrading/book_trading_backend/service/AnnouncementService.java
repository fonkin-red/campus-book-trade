package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.entity.Announcement;
import com.booktrading.book_trading_backend.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementService(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    /** 获取全部公告（前端展示用） */
    public List<Announcement> list() {
        return announcementMapper.selectAll();
    }

    /** 获取置顶公告 */
    public List<Announcement> listPinned() {
        return announcementMapper.selectPinned();
    }

    /** 发布公告 */
    public Announcement publish(Announcement announcement, Long publisherId) {
        announcement.setPublisherId(publisherId);
        announcementMapper.insert(announcement);
        return announcement;
    }

    /** 更新公告 */
    public void update(Integer id, Announcement announcement) {
        Announcement exist = announcementMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("公告不存在");
        }
        announcement.setId(id);
        announcementMapper.updateById(announcement);
    }

    /** 删除公告 */
    public void delete(Integer id) {
        announcementMapper.deleteById(id);
    }
}