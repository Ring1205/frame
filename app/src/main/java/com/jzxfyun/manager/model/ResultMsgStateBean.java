package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;

public class ResultMsgStateBean extends BaseBean {
    /**
     * data : {"pushSwitch":false,"linkMan":4,"binding":false}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pushSwitch : false
         * linkMan : 4
         * binding : false
         */

        private boolean pushSwitch;
        private int linkMan;
        private boolean binding;

        public boolean isPushSwitch() {
            return pushSwitch;
        }

        public void setPushSwitch(boolean pushSwitch) {
            this.pushSwitch = pushSwitch;
        }

        public int getLinkMan() {
            return linkMan;
        }

        public void setLinkMan(int linkMan) {
            this.linkMan = linkMan;
        }

        public boolean isBinding() {
            return binding;
        }

        public void setBinding(boolean binding) {
            this.binding = binding;
        }
    }
}
