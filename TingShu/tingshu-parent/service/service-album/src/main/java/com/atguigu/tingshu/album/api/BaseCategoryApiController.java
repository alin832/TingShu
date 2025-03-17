package com.atguigu.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.common.cache.GuiGuCache;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.album.BaseAttribute;
import com.atguigu.tingshu.model.album.BaseCategory1;
import com.atguigu.tingshu.model.album.BaseCategory3;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Tag(name = "分类管理")
@RestController
@RequestMapping(value="/api/album")
@SuppressWarnings({"all"})
public class BaseCategoryApiController {

	@Autowired
	private BaseCategoryService baseCategoryService;




	/**
	 * 查询所有的一级分类信息
	 * api/album/category/findAllCategory1
	 * @return
	 */
	@GetMapping("/category/findAllCategory1")
	public Result<List<BaseCategory1>> findAllCategory1(){

		List<BaseCategory1> category1List = baseCategoryService.list();
		return Result.ok(category1List);
	}


	/**
	 * 根据一级分类id获取全部分类信息
	 * api/album/category/getBaseCategoryList/{category1Id}
	 * @param category1Id
	 * @return
	 */
	@GetMapping("/category/getBaseCategoryList/{category1Id}")
	public Result<JSONObject> getBaseCategoryList(@PathVariable Long category1Id){

		JSONObject jsonObject=	baseCategoryService.getBaseCategoryList(category1Id);

		return Result.ok(jsonObject);
	}



	/**
	 * api/album/category/findTopBaseCategory3/{category1Id}
	 * 根据一级分类Id查询三级分类列表
	 * @param category1Id
	 * @return
	 */
	@GetMapping("/category/findTopBaseCategory3/{category1Id}")
	public Result<List<BaseCategory3>> findTopBaseCategory3(@PathVariable Long category1Id){

		List<BaseCategory3> list=baseCategoryService.findTopBaseCategory3(category1Id);

		return Result.ok(list);
	}


	/**
	 * api/album/category/getCategoryView/{category3Id}
	 * 根据三级分类Id 获取到分类信息
	 * @param category3Id
	 * @return
	 */

	@GetMapping("/category/getCategoryView/{category3Id}")
	public Result<BaseCategoryView> getCategoryView(@PathVariable Long category3Id ){

		BaseCategoryView baseCategoryView=baseCategoryService.getCategoryView(category3Id);

		return Result.ok(baseCategoryView);
	}




	/**
	 * 查询所有分类（1、2、3级分类）
	 * api/album/category/getBaseCategoryList
	 * @return
	 */
	@GetMapping("/category/getBaseCategoryList")
	public Result<List<JSONObject>> getBaseCategoryList(){

		List<JSONObject> categoryList=baseCategoryService.getBaseCategoryList();

		return Result.ok(categoryList);
	}




	/**
	 * 根据一级分类Id获取分类属性（标签）列表
	 * api/album/category/findAttribute/{category1Id}
	 * @param category1Id
	 * @return
	 */
	@GetMapping("/category/findAttribute/{category1Id}")
	public Result<List<BaseAttribute>> findAttribute(@PathVariable Long category1Id ){

		List<BaseAttribute> attributeList=baseCategoryService.findAttribute(category1Id);

		return Result.ok(attributeList);

	}



}

