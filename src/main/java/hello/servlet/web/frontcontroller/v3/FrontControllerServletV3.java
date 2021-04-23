package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV3 controller = controllerMap.get(requestURI);

        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //ex) username="김바페" , age=22 -> 이 요청을 createParamMap 메서드를
        //이용해 key(username,age),value(김바페,22) 형태로 다시 paramMap에 넣어준다.
        Map<String, String> paramMap = createParamMap(request);

        //이 후, saveController에서 map형태의 데이타를 저장한 후(process메서드)
        // 논리주소와 저장한 데이터를 ("member",member)형태로 mv에 저장후 리턴한다.
        //밑에 mv 객체에는 viewName과 model이름의 Map<"member",member>가 저장된다.
        ModelView mv = controller.process(paramMap);

        String viewName = mv.getViewName(); //여기에 viewname이 논리적이름으로 들어온다. ex) new-form
        MyView view = viewResolver(viewName); //논리+물리 주소를 모두 합해 MyView타입의 view로 전달된다.

        //mv.getModel()의 값을(Map<String,Object>) render함수에 넘겨주고
        //render함수에서는 주소로 맵핑을 해줌과 동시에 Map<"member",member> 데이터를 request에 저장시켜주고 마무리한다.
        //이 과정에서는 저장된값가 통합 주소를 결국 jsp로 넘겨서 사용해야 하기 때문에 render과정을 거친다
        view.render(mv.getModel(), request, response);
    }

    //받아온 논리 이름을 prefix, subfix를 붙여 물리주소로 만든 후 반환한다.
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator().forEachRemaining(paramName -> System.out.println("paramName = " + paramName));
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
