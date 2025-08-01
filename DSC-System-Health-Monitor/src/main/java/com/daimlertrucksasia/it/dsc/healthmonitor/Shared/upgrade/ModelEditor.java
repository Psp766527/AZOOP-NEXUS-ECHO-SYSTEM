package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;



import org.hibernate.mapping.Table;

public interface ModelEditor {
    Table clone(Table original);
}

