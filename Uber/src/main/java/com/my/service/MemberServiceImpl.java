package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.pojo.Ratings;
import com.my.mapper.*;
import com.my.pojo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private MemPasswordMapper memPasswordMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ShoppingCartElementsService shoppingCartElementsService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RatingsMapper ratingsMapper;
    @Autowired
    private RestaurantBackService restaurantBackService;


    @Override
    public List<Member> registerMember(MultipartFile multipartFile, Member member, String password, HttpSession session) {

//        獲取當前資料夾路徑
        String directoryName = System.getProperty("user.dir");
//        拼出路徑並儲存到當前專案的static的images下
        String uPhotoPrefix = "/static/images/";
        String uPhotoPath = uPhotoPrefix + multipartFile.getOriginalFilename();
        File file = new File(directoryName + "/Uber/src/main/resources/webapp" + uPhotoPath);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //判斷電話、email是否唯一，不唯一return，並(明確)顯示電話或email重複
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getMemEmail, member.getMemEmail()).or().eq(Member::getMemPhoneNum, member.getMemPhoneNum());
        List<Member> tempUser = memberMapper.selectList(wrapper);

        if (tempUser.size() == 0) {

            Member addedMember = new Member(member.getMemEmail(), member.getMemName(), member.getMemPhoneNum(), 0, uPhotoPath, null);
            memberMapper.addMember(addedMember);
            shoppingCartService.addCart(new ShoppingCart(null, member));
            memPasswordMapper.addPassword(new MemPassword(member, password));

//            剛剛是否為訪客
            if (Member.tempUser != null) {
                session.removeAttribute("position");
                session.removeAttribute("time");
                session.removeAttribute("menuType");
                session.removeAttribute("menuMoney");
                session.removeAttribute("foodClass");
                session.removeAttribute("menuConstraint");
                session.removeAttribute("currentUser");
                session.removeAttribute("keyword");
                session.removeAttribute("restaurants");
                session.removeAttribute("pageNumber");
                session.removeAttribute("maxPageNumber");
                Member.tempUser = null;
                session.removeAttribute("cartItems");
                LambdaQueryWrapper<ShoppingCart> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(ShoppingCart::getMember, addedMember.getMemEmail());
                ShoppingCart cart = shoppingCartService.getOne(wrapper1);

                for (Map.Entry<Integer, ShoppingCartElements> entry : ShoppingCart.tempCart.entrySet()) {
                    shoppingCartElementsService.addCartItem(new ShoppingCartElements(null, cart, entry.getValue().getFoodId(), entry.getValue().getFoodNum()));
                }
                ShoppingCart.tempCart.clear();
            }
        }
        return tempUser;
    }

    @Override
    public Member loginMember(String memEmail, String password, HttpSession session, HttpServletRequest request) {

        session.removeAttribute("position");
        session.removeAttribute("time");
        session.removeAttribute("menuType");
        session.removeAttribute("menuMoney");
        session.removeAttribute("foodClass");
        session.removeAttribute("menuConstraint");
        session.removeAttribute("currentUser");
        session.removeAttribute("keyword");
        session.removeAttribute("restaurants");
        session.removeAttribute("pageNumber");
        session.removeAttribute("maxPageNumber");
//        透過email去尋找會員
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getMemEmail, memEmail);
        Member tempMember = getOne(wrapper);
//        找不到email
        if (tempMember == null) {
            return null;
        }

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getMember, memEmail);
        ShoppingCart shoppingCart = shoppingCartService.getOne(queryWrapper);

//        如果找的到，繼續判斷密碼是否正確，若正確，把當前使用者保存在session裡
        MemPassword memPassword = memPasswordMapper.selectByEmail(memEmail);
        if (memPassword.getPassword().equals(password)) {
            if (tempMember.getMemType() == 1) {
                LambdaQueryWrapper<Restaurant> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(Restaurant::getOwnerEmail, memEmail);
                Restaurant restaurant = restaurantMapper.selectOne(wrapper1);
                session.setAttribute("currentUser", tempMember);
                session.setAttribute("currentRestaurant", restaurant);
                restaurantBackService.getOrderByState(request, session, 0);
                List<Order> orderItems = (List<Order>) request.getAttribute("orderItems");
                session.setAttribute("orderNumbers", orderItems.size());
                restaurantBackService.getAverRating(restaurant.getRestId(),session);
                return tempMember;
            }
            tempMember.setCartId(shoppingCart);
            session.setAttribute("currentUser", tempMember);
            if (ShoppingCart.tempCart != null) {
                if (ShoppingCart.tempCart.size() != 0) {
                    for (ShoppingCartElements cartElements : ShoppingCart.tempCart.values()) {
                        ShoppingCartElements elements = shoppingCartElementsService.getCartItemByMid(cartElements.getCartItemId());
                        if (elements == null) {
                            orderService.addCart(cartElements.getFoodId().getMenuFoodId(), session);
                        } else {
                            orderService.editCart(elements.getFoodId().getMenuFoodId(), session, cartElements.getFoodNum());
                        }
                    }
                    ShoppingCart.tempCart.clear();
                }
            }
            List<ShoppingCartElements> cartItems = shoppingCartElementsService.getAllCartItem(shoppingCart.getCartId());
            session.setAttribute("cartItems", cartItems);
            return tempMember;
        }
//        密碼不正確，直接return，並在畫面顯示uid或密碼錯誤
        return null;
    }

    @Override
    public void collectRest(Integer restId, HttpSession session, String pageNumber) {
        Member member = (Member) session.getAttribute("currentUser");
        favoriteMapper.insert(new Favorite(member.getMemEmail(), restId));
//        getFavorite(member.getMemEmail(), session);
    }

    @Override
    public void cancelCollectRest(Integer restId, HttpSession session, String pageNumber) {
        Member member = (Member) session.getAttribute("currentUser");
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getRestId, restId);
        favoriteMapper.delete(wrapper);
//        getFavorite(member.getMemEmail(), session);
    }

    @Override
    public void getFavorite(String memEmail, HttpSession session) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getMemEmail, memEmail);
        List<Favorite> favoriteList = favoriteMapper.selectList(queryWrapper);

        ArrayList<Integer> favoriteRestIds = new ArrayList<>();
        for (Favorite favorite : favoriteList) {
            favoriteRestIds.add(favorite.getRestId());
        }
        session.setAttribute("favoriteRestIds", favoriteRestIds);
    }

    @Override
    public List<Restaurant> getAllFavoriteRest(List<Integer> favoriteRestIds) {

        List<Restaurant> favoriteRests = new ArrayList<>();
        for (Integer favoriteRestId : favoriteRestIds) {
            favoriteRests.add(restaurantMapper.selectById(favoriteRestId));
        }
        return favoriteRests;
    }

    @Override
    public void goToProfile(HttpServletRequest request, HttpSession session) {
        Member currentUser = (Member) session.getAttribute("currentUser");
        MemPassword memPassword = memPasswordMapper.selectByEmail(currentUser.getMemEmail());
        request.setAttribute("password", memPassword);
    }

    @Override
    public Integer editMemberInfo(Member member, String password, HttpSession session, HttpServletRequest request) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        //檢查電話號碼重複
        wrapper.eq(Member::getMemPhoneNum, member.getMemPhoneNum());
        Member tempMember = getOne(wrapper);
        if (tempMember != null && !member.getMemEmail().equals(tempMember.getMemEmail())) {
            return 0;
        }
        //暫時儲存舊密碼
        String tempPassword = memPasswordMapper.selectByEmail(member.getMemEmail()).getPassword();
        LambdaQueryWrapper<MemPassword> queryWrapper = new LambdaQueryWrapper<>();
        //如果新密碼跟舊密碼不同，更新密碼
        if (!tempPassword.equals(password) && !password.equals("")) {
            queryWrapper.eq(MemPassword::getMember, member.getMemEmail());
            memPasswordMapper.update(new MemPassword(password), queryWrapper);
        }

        LambdaQueryWrapper<Member> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Member::getMemEmail, member.getMemEmail());
        //更新資料庫資料
        Member member1 = new Member(member.getMemName(), member.getMemPhoneNum());
        memberMapper.update(member1, wrapper1);

        Member currentUser = (Member) session.getAttribute("currentUser");
        currentUser.setMemPhoneNum(member1.getMemPhoneNum());
        currentUser.setMemName(member1.getMemName());
        return 1;
    }

    @Override
    public void getOrder(HttpSession session, HttpServletRequest request) {
        Member currentUser = (Member) session.getAttribute("currentUser");
        List<Order> orders = orderMapper.getOrderByEamil(currentUser.getMemEmail());
        List<Order> orderItem = orderService.getOrderItem(orders);
        request.setAttribute("ordersMember", orderItem);
    }

    @Override
    public void goToComment(Long orderId, String comment, Integer star, HttpSession session) {
        List<Order> currentCommentOrder = (List<Order>) session.getAttribute("currentCommentOrder");
        Member currentUser = (Member) session.getAttribute("currentUser");
        ratingsMapper.insert(new Ratings(currentUser.getMemEmail(), star, comment, orderId));
        currentCommentOrder.removeIf((order) -> order.getOrderId().equals(orderId));
        if (currentCommentOrder.size() != 0) {
            session.setAttribute("currentCommentOrder", currentCommentOrder);
        } else {
            session.removeAttribute("currentCommentOrder");
        }
        restaurantBackService.getAverRating(null,session);
    }

    @Override
    public void cancelComment(Long orderId, HttpSession session) {
        List<Order> currentCommentOrder = (List<Order>) session.getAttribute("currentCommentOrder");
        Member currentUser = (Member) session.getAttribute("currentUser");
        ratingsMapper.insert(new Ratings(currentUser.getMemEmail(), -1, "", orderId));
        currentCommentOrder.removeIf((order) -> order.getOrderId().equals(orderId));
        if (currentCommentOrder.size() != 0) {
            session.setAttribute("currentCommentOrder", currentCommentOrder);
        } else {
            session.removeAttribute("currentCommentOrder");
        }
    }
}
