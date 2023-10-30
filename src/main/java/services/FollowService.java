package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.FollowConverter;
import actions.views.FollowView;
import constants.JpaConst;
import models.Follow;

public class FollowService extends ServiceBase {

    /**
     * 指定した従業員がフォローしている従業員データを、指定されたページ数の一覧画面に表示する分取得しFollowViewのリストで返却する
     * @param followingEmployee 指定した従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<FollowView> getMinePerPage(EmployeeView followingEmployee, int page) {

        List<Follow> follows = em.createNamedQuery(JpaConst.Q_FOL_GET_ALL_MINE, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWING_EMP, EmployeeConverter.toModel(followingEmployee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return FollowConverter.toViewList(follows);
    }

    /**
     * 指定した従業員がフォローしている従業員データの件数を取得し、返却する
     * @param followingEmployee 指定した従業員
     * @return フォローしている従業員データの件数
     */
    public long countAllMine(EmployeeView followingEmployee) {

        long count = (long) em.createNamedQuery(JpaConst.Q_FOL_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWING_EMP, EmployeeConverter.toModel(followingEmployee))
                .getSingleResult();

        return count;
    }

    /**
     * 画面から入力された新しいフォロー内容を元にデータを1件作成し、フォローテーブルに登録する
     * @param fv フォローの登録内容
     */
    public void create(FollowView fv) {

        LocalDateTime ldt = LocalDateTime.now();
        fv.setCreatedAt(ldt);

        createInternal(fv);

    }

    /**
     * フォローする従業員、フォローされる従業員を条件に取得したデータをFollowViewのインスタンスで返却する
     * @param followingEmployee フォローする従業員
     * @param followerEmployee フォローされる従業員
     * @return 取得データのインスタンス 取得できない場合null
     */
    public FollowView findOne(EmployeeView followingEmployee, EmployeeView followerEmployee) {

        Follow f = null;

        try {

            f = em.createNamedQuery(JpaConst.Q_FOL_GET_BY_FOLLOWING_AND_FOLLOWER, Follow.class)
                    .setParameter(JpaConst.JPQL_PARM_FOLLOWING_EMP, EmployeeConverter.toModel(followingEmployee))
                    .setParameter(JpaConst.JPQL_PARM_FOLLOWER_EMP, EmployeeConverter.toModel(followerEmployee))
                    .getSingleResult();

        } catch (NoResultException ex) {

        }

        return FollowConverter.toView(f);

    }

    /**
     * idを条件に取得したデータをFollowViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public FollowView findOne(int id) {
        return FollowConverter.toView(findOneInternal(id));
    }

    public void destroy(Integer id) {

        FollowView removedEmp = findOne(id);

        destroyInternal(removedEmp);
    }

    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Follow findOneInternal(int id) {
        return em.find(Follow.class, id);
    }

    /**
     * フォローデータを1件登録する
     * @param fv フォローデータ
     * @return 登録結果(成功:true 失敗:false)
     */
    private void createInternal(FollowView fv) {

        em.getTransaction().begin();
        em.persist(FollowConverter.toModel(fv));
        em.getTransaction().commit();

    }

    /**
     * フォローデータを1件削除する
     * @param fv フォローデータ
     * @return 削除結果(成功:true 失敗:false)
     */
    private void destroyInternal(FollowView fv) {

        em.getTransaction().begin();
        Follow f = findOneInternal(fv.getId());
        em.remove(f);       // データ削除
        em.getTransaction().commit();

    }








}
