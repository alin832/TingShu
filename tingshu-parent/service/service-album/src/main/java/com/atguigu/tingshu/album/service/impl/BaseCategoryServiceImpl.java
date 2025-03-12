package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.mapper.*;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.common.cache.GuiGuCache;
import com.atguigu.tingshu.model.album.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"all"})
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategoryService {

	@Autowired
	private BaseCategory1Mapper baseCategory1Mapper;

	@Autowired
	private BaseCategory2Mapper baseCategory2Mapper;

	@Autowired
	private BaseCategory3Mapper baseCategory3Mapper;

	@Autowired
	private BaseAttributeMapper baseAttributeMapper;

	@Autowired
	private BaseCategoryViewMapper baseCategoryViewMapper;

	/**
	 * 查询所有分类（1、2、3级分类）
	 * @return
	 */
	@Override
	@GuiGuCache(prefix = "BaseCategoryListView:")
	public List<JSONObject> getBaseCategoryList() {
		//创建集合一级分类，收集数据
		List<JSONObject> allList=new ArrayList<>();

		//查询所有分类信息
		List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(null);
		//判断
		if(CollectionUtil.isNotEmpty(baseCategoryViewList)){

			//分组一级分类
			Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

			//遍历一级分类分组数据
			for (Map.Entry<Long, List<BaseCategoryView>> entry1 : category1Map.entrySet()) {

				//创建一级分类封装对象
				JSONObject obj1=new JSONObject();

				//获取一级分类ID
				Long category1Id = entry1.getKey();
				obj1.put("categoryId",category1Id);
				//获取一级分类name
				List<BaseCategoryView> category2List = entry1.getValue();
				String category1Name = category2List.get(0).getCategory1Name();
				obj1.put("categoryName",category1Name);

				//创建二级分类收集集合
				List<JSONObject> array2=new ArrayList<>();

				//分组二级分类集合
				Map<Long, List<BaseCategoryView>> category2Map = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));

				//遍历二级分类分组数据
				for (Map.Entry<Long, List<BaseCategoryView>> entry2 : category2Map.entrySet()) {

					//创建二级分类封装对象
					JSONObject obj2=new JSONObject();
					//获取二级分类ID
					Long category2Id = entry2.getKey();
					obj2.put("categoryId",category2Id);
					//获取二分类name
					List<BaseCategoryView> category3List = entry2.getValue();
					String category2Name = category3List.get(0).getCategory2Name();
					obj2.put("categoryName",category2Name);

					//封装三级分类集合
					List<JSONObject> array3 = category3List.stream().map(baseCategoryView -> {

						JSONObject obj3 =
								new JSONObject();
						obj3.put("categoryId", baseCategoryView.getCategory3Id());
						obj3.put("categoryName", baseCategoryView.getCategory3Name());

						return obj3;
					}).collect(Collectors.toList());


					obj2.put("categoryChild",array3);

					//收集二级分类
					array2.add(obj2);


				}


				//封装二级分类
				obj1.put("categoryChild",array2);

				//收集一级分类对象
				allList.add(obj1);

			}


		}


		return allList;
	}

	/**
	 * 根据一级分类Id获取分类属性（标签）列表
	 * @param category1Id
	 * @return
	 */
	@Override
	public List<BaseAttribute> findAttribute(Long category1Id) {


		return baseAttributeMapper.selectAttribute(category1Id);
	}

	/**
	 * 根据三级分类Id 获取到分类信息
	 * @param category3Id
	 * @return
	 */
	@Override
	@GuiGuCache(prefix = "categoryView:")
	public BaseCategoryView getCategoryView(Long category3Id) {
		return baseCategoryViewMapper.selectById(category3Id);
	}

	/**
	 * 根据一级分类Id查询三级分类列表
	 * @param category1Id
	 * @return
	 */
	@Override
	public List<BaseCategory3> findTopBaseCategory3(Long category1Id) {
		//根据一级分类ID查询所属二级分类集合

		List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));

		//根据二级分类集合过滤出二级分类ID集合
		List<Long> category2IdList = baseCategory2List.stream().map(baseCategory2 -> baseCategory2.getId()).collect(Collectors.toList());

		//构建查询条件对象
		QueryWrapper<BaseCategory3> queryWrapper=new QueryWrapper<>();
		//添加二级分类集合ID
		queryWrapper.in("category2_id",category2IdList);
		//筛选可以置顶的分类
		queryWrapper.eq("is_top",1);
		//添加排序
		queryWrapper.orderByAsc("order_num");
		//只获取前7个分类
		queryWrapper.last(" limit 7 ");

		//查询三级分类数据

		List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(queryWrapper);


		return baseCategory3List;
	}

	/**
	 * 根据一级分类id获取全部分类信息
	 * @param category1Id
	 * @return
	 */
	@Override
	@GuiGuCache(prefix = "baseCategoryLis:")
	public JSONObject getBaseCategoryList(Long category1Id) {

		//创建封装对象
		JSONObject object=new JSONObject();
		//创建指定一级分类对应的视图数据
		List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(new QueryWrapper<BaseCategoryView>().eq("category1_id", category1Id));
		//封装一级分类数据
		if(CollectionUtil.isNotEmpty(baseCategoryViewList)){

			//设置一级分类id
			Long category1Id1 = baseCategoryViewList.get(0).getCategory1Id();
			object.put("categoryId",category1Id1);
			//设置一级分类name
			String category1Name = baseCategoryViewList.get(0).getCategory1Name();
			object.put("categoryName",category1Name);
			//创建二级分类封装集合
			List<JSONObject> category2List=new ArrayList<>();

			//集合数据分组
			Map<Long, List<BaseCategoryView>> cateogry2Map = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
			//遍历集合
			for (Map.Entry<Long, List<BaseCategoryView>> entry : cateogry2Map.entrySet()) {

				//创建封装对象
				JSONObject object2=new JSONObject();
				//获取二级分类ID
				Long category2Id = entry.getKey();
				object2.put("categoryId",category2Id);
				//获取二分类name
				List<BaseCategoryView> category3List = entry.getValue();
				String category2Name = category3List.get(0).getCategory2Name();
				object2.put("categoryName",category2Name);

				//获取三级分类集合
				List<JSONObject> categoryList3 = category3List.stream().map(baseCategoryView -> {
					//创建封装数据
					JSONObject object3 = new JSONObject();
					object3.put("categoryId", baseCategoryView.getCategory3Id());
					object3.put("categoryName", baseCategoryView.getCategory3Name());


					return object3;
				}).collect(Collectors.toList());

				//设置三级分类集合
				object2.put("categoryChild",categoryList3);

				category2List.add(object2);

			}


			//设置一级分类categoryChild
			object.put("categoryChild",category2List);


		}


		return object;
	}
}
