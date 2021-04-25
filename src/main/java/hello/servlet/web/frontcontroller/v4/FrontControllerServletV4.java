package hello.servlet.web.frontcontroller.v4;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {

    private Map<String, ControllerV4> controllerMap = new HashMap<>();

    public FrontControllerServletV4() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV4 controller = controllerMap.get(requestURI);

        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //ex) username="김바페" , age=22 -> 이 요청을 createParamMap 메서드를
        //이용해 key(username,age),value(김바페,22) 형태로 다시 paramMap에 넣어준다.
        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>(); //추가

        //이 후, saveController에서 map형태의 데이타를 저장한 후(process메서드) model에 객체 저장 후 viewName만 return해준다.
        String viewName = controller.process(paramMap, model);

        MyView view = viewResolver(viewName); //논리+물리 주소를 모두 합해 MyView타입의 view로 전달된다.

        //render함수에서는 주소로 맵핑을 해줌과 동시에 Map<"member",member> 데이터를 Map형태의 model에 담아 render한다.
        //이 과정에서는 저장된값가 통합 주소를 결국 jsp로 넘겨서 사용해야 하기 때문에 render과정을 거친다
        view.render(model, request, response);
    }


    //받아온 논리 이름을 prefix, subfix를 붙여 물리주소로 만든 후 반환한다.
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    //save폼에서 값 입력 후 전송버튼을 누르면 먼저 이메서드를 호출시킨다.
    //form에서 넘어온 값들을 request.getParameterNames()와 request.getParameter()메서드를 호출하여 Map에 저장시킨다.
    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        //출력문장
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> System.out.println(paramName +"="+ request.getParameter(paramName)));


        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
