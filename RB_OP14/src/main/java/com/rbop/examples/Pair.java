package com.rbop.examples;

import java.util.*;

public class Pair<L,R> {
        private L l;
        private R r;
        public Pair(){
            this.l = l;
            this.r = r;
        }
        public L getL(){ return l; }
        public R getR(){ return r; }
        public void setL(L l){ this.l = l; }
        public void setR(R r){ this.r = r; }

}