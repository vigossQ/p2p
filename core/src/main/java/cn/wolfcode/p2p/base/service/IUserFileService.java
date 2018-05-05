package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.UserFile;
import cn.wolfcode.p2p.base.query.QueryObject;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IUserFileService {
    int save(UserFile userFile);

    int update(UserFile userFile);

    UserFile get(Long id);

    PageInfo queryPage(QueryObject qo);

    /**
     * 上传风控材料
     * @param imagePath
     */
    void apply(String imagePath);

    /**
     * 查询当前用户没有选择风控类型的记录
     * @return
     */
    List<UserFile> queryUnSelectFileTypeList();

    /**
     * 设置风控类型
     * @param ids
     * @param fileTypes
     */
    void selectType(Long[] ids, Long[] fileTypes);

    /**
     * 查询当前用户选择风控类型的记录
     * @return
     */
    List<UserFile> querySelectFileTypeList();

    /**
     * 风控材料审核
     * @param id
     * @param state
     * @param score
     * @param remark
     */
    void audit(Long id, int state, int score, String remark);

    List<UserFile> queryByLogininfoId(Long logininfoId, int state);
}
