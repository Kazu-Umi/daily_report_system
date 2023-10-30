package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.FollowView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.EmployeeService;
import services.FollowService;

public class FollowAction extends ActionBase {

    private EmployeeService employeeService;
    private FollowService followService;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        employeeService = new EmployeeService();
        followService = new FollowService();

        //メソッドを実行
        invoke();
        employeeService.close();
        followService.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン

        //セッションからログイン中の従業員情報を取得
        EmployeeView loginEmployee = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //ログイン中の従業員のフォローデータを、指定されたページ数の一覧画面に表示する分取得する
        int page = getPage();
        List<FollowView> follows = followService.getMinePerPage(loginEmployee, page);

        //ログイン中の従業員が作成したフォローデータの件数を取得
        long myFollowsCount = followService.countAllMine(loginEmployee);

        putRequestScope(AttributeConst.FOLLOWS, follows); //取得したフォローデータ
        putRequestScope(AttributeConst.FOL_COUNT, myFollowsCount); //ログイン中の従業員のフォローしている従業員の数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

      //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_FOL_INDEX);

    }

    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //セッションからログイン中の従業員情報を取得
            EmployeeView followingEv = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

            //idを条件に従業員データを取得する
            EmployeeView followerEv = employeeService.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            //パラメータの値をもとにフォロー情報のインスタンスを作成する
            FollowView fv = new FollowView(
                    null,
                    followingEv, //ログインしている従業員を、フォローする従業員として登録する
                    followerEv,
                    null);

            followService.create(fv);

            //セッションに登録完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_FOL, ForwardConst.CMD_INDEX);

        }

    }

    /**
     * 削除を行う
     * @throws ServletException
     * @throws IOException
     */
    public void destroy() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //選択した従業員を条件にフォローデータを削除する
            followService.destroy(toNumber(getRequestParam(AttributeConst.FOL_ID)));

            //セッションに削除完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_FOL, ForwardConst.CMD_INDEX);
        }
    }



}
