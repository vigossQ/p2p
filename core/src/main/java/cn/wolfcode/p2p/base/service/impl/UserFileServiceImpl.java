package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.domain.SystemDictionaryItem;
import cn.wolfcode.p2p.base.domain.UserFile;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.mapper.UserFileMapper;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.base.service.IUserFileService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserFileServiceImpl implements IUserFileService {

    @Autowired
    private UserFileMapper userFileMapper;
    @Autowired
    private IUserinfoService userinfoService;

    @Override
    public int save(UserFile userFile) {
        return userFileMapper.insert(userFile);
    }

    @Override
    public int update(UserFile userFile) {
        return userFileMapper.updateByPrimaryKey(userFile);
    }

    @Override
    public UserFile get(Long id) {
        return userFileMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<UserFile> list = userFileMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public void apply(String imagePath) {
        UserFile uf = new UserFile();
        uf.setImage(imagePath);
        uf.setApplier(UserContext.getCurrent());
        uf.setApplyTime(new Date());
        uf.setState(UserFile.STATE_NORMAL);
        this.save(uf);
    }

    @Override
    public List<UserFile> queryUnSelectFileTypeList() {
        return userFileMapper.queryUnSelectFileTypeList(UserContext.getCurrent().getId());
    }

    @Override
    public void selectType(Long[] ids, Long[] fileTypes) {
        if (ids != null && fileTypes != null && ids.length == fileTypes.length) {
            UserFile uf;
            SystemDictionaryItem fileType;
            for (int i = 0; i < ids.length; i++) {
                uf = this.get(ids[i]);
                //只能修改自己的记录
                if (uf.getApplier().getId().equals(UserContext.getCurrent().getId())) {
                    fileType = new SystemDictionaryItem();
                    fileType.setId(fileTypes[i]);
                    uf.setFileType(fileType);
                    this.update(uf);
                }
            }
        }
    }

    @Override
    public List<UserFile> querySelectFileTypeList() {
        return userFileMapper.querySelectFileTypeList(UserContext.getCurrent().getId());
    }

    @Override
    public void audit(Long id, int state, int score, String remark) {
        UserFile userFile = this.get(id);
        //userFile不为null,处于审核状态
        if (userFile != null && userFile.getState() == UserFile.STATE_NORMAL) {
            //设置审核相关属性
            userFile.setAuditor(UserContext.getCurrent());
            userFile.setAuditTime(new Date());
            userFile.setRemark(remark);

            if (state == UserFile.STATE_PASS) {
                //如果审核通过
                userFile.setState(UserFile.STATE_PASS);
                //设置分数
                userFile.setScore(score);
                //更新用户分数
                Userinfo userinfo = userinfoService.get(userFile.getApplier().getId());
                userinfo.setScore(userinfo.getScore() + score);
                userinfoService.update(userinfo);
            } else {
                //如果审核拒绝
                userFile.setState(UserFile.STATE_REJECT);
            }
            this.update(userFile);
        }
    }

    @Override
    public List<UserFile> queryByLogininfoId(Long logininfoId, int state) {
        return this.userFileMapper.queryByLogininfoId(logininfoId,state);
    }
}
