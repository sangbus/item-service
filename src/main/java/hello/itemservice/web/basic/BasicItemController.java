package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
/**
 * url에 해당하는 단어가 있으면 커넥 시켜주는 클래스
 * */
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items",items);
        return "/basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item",item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm(){
        return "basic/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model)
    {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);
        model.addAttribute("item",item);
        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item)
    {
        itemRepository.save(item);
//        model.addAttribute("item",item); ModelAttribute가 자동 추가하기 때문에 생략 가능
        // ModelAttribute는 Item 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setXXX)으로 입력해준다.
        // Model에 ModelAttribute로 지정한 객체를 자동으로 넣어준다.
        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item)
    {
        itemRepository.save(item);
        // ModelAttribute에 암것도 지정하지 않으면 클래스명의 첫글자를 소문자로 바꿔서 넣음
        // 지금은 Item -> item으로 넣는다.
        return "basic/item";
    }

//    @PostMapping("/add")
    // ModelAttribute는 Item과 같은 임의의 타입은 생략 가능
    // String, int 같은 타입은 RequestParam으로 적용됨
    public String addItemV4(Item item)
    {
        itemRepository.save(item);
        return "basic/item";
    }

//    @PostMapping("/add")
    // ModelAttribute는 Item과 같은 임의의 타입은 생략 가능
    // String, int 같은 타입은 RequestParam으로 적용됨

    /**
     * PRG(Post Redirect Get) 방식
     * 새로고침하면 마지막에 실행한 행동을 다시 함
     * 그래서 등록된 상품이 재등록되는 사고가 발생
     * 그것을 막기 위해 PRG 방식을 사용한다.
     * 마지막으로 Get을 호출하여 재등록을 막음
     * */
    public String addItemV5(Item item)
    {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId();
    }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes)
    {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        redirectAttributes.addAttribute("addStatus",true);
        return "redirect:/basic/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item",item);
        return "basic/editForm";
    }
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item, RedirectAttributes redirectAttributes){
        itemRepository.update(itemId,item);
        redirectAttributes.addAttribute("itemId",itemId);
        redirectAttributes.addAttribute("editStatus",true);
        return "redirect:/basic/items/{itemId}"; // 상품 상세 페이지 재호출
        // 리다이렉트에 대해 HTTP 수업에서 자세히 다뤘다고 하는데 기억이 안 남
    }

    /**
     * 테스트용 데이터 추가
     * */
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA",10000,10));
        itemRepository.save(new Item("itemB",20000,20));
    }
}
