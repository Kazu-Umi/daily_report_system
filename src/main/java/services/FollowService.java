package services;

import java.time.LocalDateTime;
import java.util.List;

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
     * @param followingEmployee
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
     * フォローデータを1件登録する
     * @param fv フォローデータ
     */
    private void createInternal(FollowView fv) {

        em.getTransaction().begin();
        em.persist(FollowConverter.toModel(fv));
        em.getTransaction().commit();

    }

}
