package com.ict.txtprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class FBSegment {
	private Set<String> seg_dict;
	
	public void setSegDict(Set<String> srcDict){
		this.seg_dict = srcDict;
	}
	
	//加载词典
	public void Init(){
		seg_dict = new HashSet<String>();
		String dicpath = "data/worddic.txt";
		String line = null;
		
		BufferedReader br;
		try{
			br = new BufferedReader( new InputStreamReader( new FileInputStream(dicpath)));	
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.isEmpty())
					continue;	
				seg_dict.add(line);
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 前向算法分词
	 * @param seg_dict 分词词典
	 * @param phrase 待分词句子
	 * @return 前向分词结果
	 */
	private Vector<String> FMM2( String  phrase){
		int maxlen = 16;
		Vector<String> fmm_list = new Vector<String>();
		int len_phrase = phrase.length();
		int i=0,j=0;
		
		while(i < len_phrase){
			int end = i+maxlen;
			if(end >= len_phrase)
				end = len_phrase;
			String phrase_sub = phrase.substring(i, end);
			for(j = phrase_sub.length(); j >=0; j--){
				if(j == 1)
					break;
				String key =  phrase_sub.substring(0, j);
				if(seg_dict.contains(key)){
					fmm_list.add(key);
					i +=key.length() -1;
					break;
				}
			}
			if(j == 1)
				fmm_list.add(""+phrase_sub.charAt(0));
			i+=1;
		}
		return fmm_list;
	}
	
	/**
	 * 后向算法分词
	 * @param seg_dict 分词词典
	 * @param phrase 待分词句子
	 * @return 后向分词结果
	 */
	private Vector<String> BMM2( String  phrase){
		int maxlen = 16;
		Vector<String> bmm_list = new Vector<String>();
		int len_phrase = phrase.length();
		int i=len_phrase,j=0;
		
		while(i > 0){
			int start = i - maxlen;
			if(start < 0)
				start = 0;
			String phrase_sub = phrase.substring(start, i);
			for(j = 0; j < phrase_sub.length(); j++){
				if(j == phrase_sub.length()-1)
					break;
				String key =  phrase_sub.substring(j);
				if(seg_dict.contains(key)){
					bmm_list.insertElementAt(key, 0);
					i -=key.length() -1;
					break;
				}
			}
			if(j == phrase_sub.length() -1)
				bmm_list.insertElementAt(""+phrase_sub.charAt(j), 0);
			i -= 1;
		}
		return bmm_list;
	}
		
	/**
	 * 该方法结合正向匹配和逆向匹配的结果，得到分词的最终结果
	 * @param FMM2 正向匹配的分词结果
	 * @param BMM2 逆向匹配的分词结果
	 * @param return 分词的最终结果
	 */
	public Vector<String> segment( String phrase){
		Vector<String> fmm_list = FMM2(phrase);
		Vector<String> bmm_list = BMM2(phrase);
		//如果正反向分词结果词数不同，则取分词数量较少的那个
		if(fmm_list.size() != bmm_list.size()){
			if(fmm_list.size() > bmm_list.size())
				return bmm_list;
			else return fmm_list;
		}
		//如果分词结果词数相同
		else{
			//如果正反向的分词结果相同，就说明没有歧义，可返回任意一个
			int i ,FSingle = 0, BSingle = 0;
			boolean isSame = true;
			for(i = 0; i < fmm_list.size();  i++){
				if(!fmm_list.get(i).equals(bmm_list.get(i)))
					isSame = false;
				if(fmm_list.get(i).length() ==1)
					FSingle +=1;
				if(bmm_list.get(i).length() ==1)
					BSingle +=1;
			}
			if(isSame)
				return fmm_list;
			else{
				//分词结果不同，返回其中单字较少的那个
				if(BSingle > FSingle)
					return fmm_list;
				else return bmm_list;
			}
		}
	}
	
//	public static void main(String [] args){
//		String test = "我是一个学生";
//		FBSegment.Init();
//		System.out.println(FBSegment.segment(test));
//	}
	
}