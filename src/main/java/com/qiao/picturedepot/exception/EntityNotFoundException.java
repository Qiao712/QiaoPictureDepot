package com.qiao.picturedepot.exception;

public class EntityNotFoundException extends BusinessException{
    private Class<?> entityClass;

    public EntityNotFoundException(){
    }
    public EntityNotFoundException(Class<?> entityClass){
        this.entityClass = entityClass;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getMessage() {
        if(entityClass != null){
            return "目标实体" + entityClass.getSimpleName() + "不存在";
        }else{
            return "目标实体不存在";
        }
    }
}
