import React, { useCallback, useState, useEffect } from 'react';
import { useFocusEffect, useNavigation } from '@react-navigation/native';
import { MaterialCommunityIcons } from '@expo/vector-icons';
import { StyleSheet, View, Text, Pressable, FlatList, Image } from "react-native"
import TokenUtils from "../../stores/TokenUtils";
import { getHomeOnlyPay } from "../../apis/HomeApi";


function OnlyPay() {
  const navigation = useNavigation()
  const [token, setToken] = useState('')
  const [month, setMonth] = useState(new Date().getMonth() + 1)
  const [year, setYear] = useState(new Date().getFullYear())
  const [payHistory, setPayHistory] = useState([])
  const [payAmount, setPayAmount] = useState('')
  const imgPath = [
    { categoryId: "대중교통", path: require('../../../assets/CategoryIcon/1.png') },
    { categoryId: "주유", path: require('../../../assets/CategoryIcon/2.png') },
    { categoryId: "마트", path: require('../../../assets/CategoryIcon/3.png') },
    { categoryId: "편의점", path: require('../../../assets/CategoryIcon/4.png') },
    { categoryId: "통신", path: require('../../../assets/CategoryIcon/5.png') },
    { categoryId: "온라인쇼핑", path: require('../../../assets/CategoryIcon/6.png') },
    { categoryId: "쇼핑", path: require('../../../assets/CategoryIcon/7.png') },
    { categoryId: "배달앱", path: require('../../../assets/CategoryIcon/8.png') },
    { categoryId: "음식점", path: require('../../../assets/CategoryIcon/9.png') },
    { categoryId: "주점", path: require('../../../assets/CategoryIcon/10.png') },
    { categoryId: "카페", path: require('../../../assets/CategoryIcon/11.png') },
    { categoryId: "디저트", path: require('../../../assets/CategoryIcon/12.png') },
    { categoryId: "뷰티/피트니스", path: require('../../../assets/CategoryIcon/13.png') },
    { categoryId: "공과금", path: require('../../../assets/CategoryIcon/14.png') },
    { categoryId: "병원/약국", path: require('../../../assets/CategoryIcon/15.png') },
    { categoryId: "애완동물", path: require('../../../assets/CategoryIcon/16.png') },
    { categoryId: "교육", path: require('../../../assets/CategoryIcon/17.png') },
    { categoryId: "자동차", path: require('../../../assets/CategoryIcon/18.png') },
    { categoryId: "레저/스포츠", path: require('../../../assets/CategoryIcon/19.png') },
    { categoryId: "영화", path: require('../../../assets/CategoryIcon/20.png') },
    { categoryId: "문화/여가", path: require('../../../assets/CategoryIcon/21.png') },
    { categoryId: "간편결제", path: require('../../../assets/CategoryIcon/22.png') },
    { categoryId: "항공", path: require('../../../assets/CategoryIcon/23.png') },
    { categoryId: "여행/숙박", path: require('../../../assets/CategoryIcon/24.png') },
    { categoryId: "기타", path: require('../../../assets/CategoryIcon/25.png') },
  ]

  useEffect(() => {
    const fetchToken = async () => {
      const accessToken = await TokenUtils.getAccessToken();
      setToken(accessToken);
    };

    fetchToken();
  }, []);

  const fetchHistoryList = useCallback(async () => {
    if (token) {
      try {
        const res = await getHomeOnlyPay(token, `${year}${month.toString().padStart(2, '0')}`);
        setPayHistory(res.data.list);
        setPayAmount(res.data.totalAmt);
      } catch (err) {
        console.error(err);
      }
    }
  }, [token, month, year]);

  useFocusEffect(
    useCallback(() => {
      fetchHistoryList();
    }, [fetchHistoryList])
  );

  const changeMonth = (delta) => {
    let newMonth = month + delta;
    let newYear = year;

    // 현재 날짜 정보를 가져옴
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const currentMonth = currentDate.getMonth() + 1;

    // 미래로 넘어가려는 경우를 체크하여 막음
    if (newYear > currentYear || (newYear === currentYear && newMonth > currentMonth)) {
      return; // 현재 날짜 이후로는 변경하지 않음
    }

    // 이전 연도로 넘어가는 경우
    if (newMonth < 1) {
      if (newYear > currentYear - 1) { // 최소 한 해 전으로만 가능
        newMonth = 12;
        newYear -= 1;
      } else {
        return; // 한 해 이전 이상으로는 변경하지 않음
      }
    }

    // 다음 연도로 넘어가는 경우 (이미 위에서 미래로 넘어가는 경우를 막았으므로 추가 조건은 필요 없음)
    if (newMonth > 12) {
      newMonth = 1;
      newYear += 1;
    }

    setMonth(newMonth);
    setYear(newYear);
  };

  function transformData(list) {
    if (!list || list.length === 0) {
      return [];
    }

    const groupedByDate = list.reduce((acc, cur) => {
      const date = new Date(cur.approvedDateTime);
      const day = date.toLocaleDateString('ko-KR', { weekday: 'short' });
      const formattedDate = `${date.getDate()}`;

      const item = {
        category: cur.largeCategoryName,
        title: cur.smallCategoryName,
        card: cur.userCardId,
        price: cur.approvedAmt,
      };

      if (!acc[formattedDate]) {
        acc[formattedDate] = {
          date: parseInt(formattedDate, 10),
          day,
          info: [item],
        };
      } else {
        acc[formattedDate].info.push(item);
      }

      return acc;
    }, {});

    return Object.values(groupedByDate).sort((a, b) => b.date - a.date);
  }

  const transformedData = transformData(payHistory);
  // console.log(transformedData);


  const renderDateItem = ({ item }) => {
    return (
      <View style={{ marginVertical: 20 }}>
        <Text style={{ fontSize: 18, fontWeight: 'bold', marginLeft: 30, color: '#999999' }}>
          {item?.date}일 {item?.day}
        </Text>
        <View style={{ marginVertical: 10, alignSelf: 'center', width: 360, height: 1, backgroundColor: '#999999' }}></View>
        <FlatList
          data={item.info}
          renderItem={renderInfoItem}
          keyExtractor={(item, index) => index.toString()}
        />
      </View>
    );
  };

  const renderInfoItem = ({ item }) => {
    const imagePath = imgPath.find(path => path.categoryId === item.category)?.path || require('../../../assets/favicon.png');
    return (
      <View style={{ marginLeft: 20, marginTop: 10, flexDirection: 'row', alignItems: 'center' }}>
        <Image source={imagePath} style={{ width: 50, height: 50 }} />
        {/* 찾은 이미지 경로를 사용 */}
        <View style={{ paddingLeft: 10, width: '45%'}}>
          <Text style={styles.place}>{item.title}</Text>
          <Text style={styles.anotherinfo}>{item.category} | {item.card}</Text>
        </View>
        <View style={{ width: '40%', height:'100%', alignItems:'flex-end', paddingTop: 5}}>
          <Text style={[styles.price, {fontSize:18}]}>{item.price.toLocaleString()}원</Text>
        </View>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.backcontainer}>
        <Pressable onPress={() => navigation.navigate('Home')}>
          <MaterialCommunityIcons
            name="chevron-left" size={50} color={'#B8B8B8'} />
        </Pressable>
        <Text style={styles.title}>이번 달 어플 결제 금액</Text>
      </View>

      <View style={styles.midcontainer}>
        <Pressable onPress={() => changeMonth(-1)}>
          <MaterialCommunityIcons name="chevron-left" size={50} color={'#4F4F4F'} />
        </Pressable>
        <Text style={styles.title}>{year}년 {month.toString().padStart(2, '0')}월</Text>
        <Pressable onPress={() => changeMonth(1)}>
          <MaterialCommunityIcons name="chevron-right" size={50} color={'#4F4F4F'} style={styles.nextBtn} />
        </Pressable>
      </View>

      <View style={styles.wholecontainer}>
        <Text style={styles.wholetext}>어플 결제 총 금액</Text>
        <Text style={styles.wholeprice}>{payAmount.toLocaleString()} 원</Text>
      </View>

      <FlatList
        data={transformedData}
        renderItem={renderDateItem}
        keyExtractor={(item, index) => index.toString()}
      />
    </View>
  )
}

export default OnlyPay

const styles = StyleSheet.create({
  container: {
    marginTop: 60,
  },
  title: {
    fontWeight: 'bold',
    fontSize: 24,
    marginStart: 20,
  },
  backcontainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 10,
  },
  midcontainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginVertical: 20,
  },
  wholecontainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginVertical: 40,
    alignItems: 'center',
  },
  wholetext: {
    fontWeight: '800',
    fontSize: 20,
    marginStart: 40,
  },
  wholeprice: {
    fontWeight: '800',
    fontSize: 24,
    marginEnd: 40,
  },
  nextBtn: {
    marginHorizontal: 16,
  },
  place: {
    fontSize: 16,
    fontWeight: '800'
  },
  anotherinfo: {
    color: '#999999'
  },
  price: {
    fontSize: 20,
    fontWeight: 'bold',
    marginHorizontal: 20,
  }
})