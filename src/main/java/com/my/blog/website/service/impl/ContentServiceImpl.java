package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dao.ContentVoMapper;
import com.my.blog.website.enums.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.service.IRelationshipService;
import com.my.blog.website.untils.DateKit;
import com.my.blog.website.untils.TaleUtils;
import com.my.blog.website.untils.Tools;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentServiceImpl implements IContentService {
    private static final Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);
    @Resource
    private ContentVoMapper contentVoMapper;

    @Autowired
    private IMetaService metaService;

    @Resource
    private IRelationshipService relationshipService;

    @Override
    public PageInfo<ContentVo> getArticlesWithPage(ContentVoExample example, int page, int limit) {
        PageHelper.startPage(page, limit);
        List<ContentVo> contentVoList = contentVoMapper.selectByExampleWithBLOBs(example);
        return new PageInfo<>(contentVoList);
    }

    @Override
    public ContentVo getContents(String cid) {
        if (StringUtils.isNotBlank(cid)) {
            if (Tools.isNumber(cid)) {
                return contentVoMapper.selectByPrimaryKey(Integer.valueOf(cid));
            } else {
                ContentVoExample example = new ContentVoExample();
                example.createCriteria().andSlugEqualTo(cid);
                List<ContentVo> list = contentVoMapper.selectByExampleWithBLOBs(example);
                if (list.size() != 1) {
                    throw new TipException("query content by id and return is not one");
                }
                return list.get(0);
            }
        }
        return null;
    }

    /**
     * @param [contentVo]
     * @return java.lang.String
     * @author rfYang
     * @date 2018/6/8 16:20
     */
    @Override
    @Transactional
    public String publish(ContentVo contentVo) {
        if (contentVo == null) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(contentVo.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(contentVo.getContent())) {
            return "文章类容不能为空";
        }
        if (contentVo.getTitle().length() > WebConst.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        if (contentVo.getContent().length() > WebConst.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        if (contentVo.getAuthorId() == null) {
            return "请登陆后再发表文章";
        }
        if (StringUtils.isNotBlank(contentVo.getSlug())) {
            if (contentVo.getSlug().length() < 5) {
                return "路径太短了";
            }
            if (!TaleUtils.isPath(contentVo.getSlug())) {
                return "您输入的不是合法路径";
            }
            ContentVoExample example = new ContentVoExample();
            example.createCriteria().andTypeEqualTo(contentVo.getType()).andSlugEqualTo(contentVo.getSlug());
            long count = contentVoMapper.countByExample(example);
            if (count == 1) {
                return "该路径已经存在";
            }
        } else {
            contentVo.setSlug(null);
        }
        contentVo.setContent(EmojiParser.parseToAliases(contentVo.getContent()));//处理文章的emoji表情包
        contentVo.setCommentsNum(0);
        contentVo.setHits(0);
        int time = DateKit.getCurrentUnixTime();
        contentVo.setCreated(time);
        contentVo.setModified(time);
        contentVoMapper.insert(contentVo);

        String categories = contentVo.getCategories();
        String tags = contentVo.getTags();
        Integer cid = contentVo.getCid();
        metaService.saveMetas(cid, tags, Types.TAG.getType());
        metaService.saveMetas(cid, categories, Types.CATEGORY.getType());

        return WebConst.SUCCESS_RESULT;
    }

    /**
     * 修改用户博文
     * @author rfYang
     * @date 2018/6/11 9:04
     * @param [contentVo]
     * @return java.lang.String
     */
    @Override
    @Transactional
    public String updateArticle(ContentVo contents) {
        if (null == contents) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(contents.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = contents.getTitle().length();
        if (titleLength > WebConst.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = contents.getContent().length();
        if (contentLength > WebConst.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        if (null == contents.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isBlank(contents.getSlug())) {
            contents.setSlug(null);
        }

        int time = DateKit.getCurrentUnixTime();
        contents.setModified(time);
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));//处理文章的emoji表情包

        Integer cid = contents.getCid();
        contentVoMapper.updateByPrimaryKeySelective(contents);
        relationshipService.deleteById(cid, null);
        metaService.saveMetas(cid, contents.getTags(), Types.TAG.getType());
        metaService.saveMetas(cid, contents.getCategories(), Types.CATEGORY.getType());
        return WebConst.SUCCESS_RESULT;
    }

    /**
     * 删除文章
     * @author rfYang
     * @date 2018/6/11 9:30
     * @param [cid]
     * @return java.lang.String
     */
    @Override
    @Transactional
    public String deleteByCid(Integer cid) {
        //cid是否有对应的博文
        ContentVo contentVo = this.getContents(cid+"");
        if(contentVo!=null){
            int result = contentVoMapper.deleteByPrimaryKey(cid);
            if(result==1){
                relationshipService.deleteById(cid, null);
                return WebConst.SUCCESS_RESULT;
            }else{
                return "博文条目数有错";
            }
        }
        return "博文为空";
    }


    @Override
    public void updateContentByCid(ContentVo contentVo) {
        if (null != contentVo && null != contentVo.getCid()) {
            contentVoMapper.updateByPrimaryKeySelective(contentVo);
        }
    }
}
