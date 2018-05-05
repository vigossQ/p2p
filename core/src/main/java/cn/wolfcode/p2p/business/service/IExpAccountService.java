package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.ExpAccount;
import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Reimu on 2018/4/7.
 */
public interface IExpAccountService {
    int save(ExpAccount expAccount);

    int update(ExpAccount expAccount);

    ExpAccount get(Long id);

    /**
     * 发放体验金
     *
     * @param id           体验金账户id
     * @param lastTime     体验金有效期
     * @param expmoney     体验金金额
     * @param expmoneyType 体验金发放类型
     */
    void grantExpMoney(Long id, LastTime lastTime, BigDecimal expmoney, int expmoneyType);

    /**
     * 有效期
     */
    @Getter
    class LastTime {
        private int amount;
        private LastTimeUnit unit;

        public LastTime(int amount, LastTimeUnit unit) {
            this.amount = amount;
            this.unit = unit;
        }

        /**
         * 获取一个到期时间
         * @param date
         * @return
         */
        public Date getReturnDate(Date date) {
            switch (this.unit) {
                case DAY:
                    return DateUtils.addDays(date, this.amount);
                case MONTH:
                    return DateUtils.addMonths(date, this.amount);
                case YEAR:
                    return DateUtils.addYears(date, this.amount);
                default:
                    return date;
            }
        }
    }

    /**
     * 持续时间单位
     */
    enum LastTimeUnit {
        DAY, MONTH, YEAR
    }
}
